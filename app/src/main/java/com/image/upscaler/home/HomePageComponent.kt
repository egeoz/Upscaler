package com.image.upscaler.home

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.documentfile.provider.DocumentFile
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.image.realesrgan.UpscalingModel
import com.image.upscaler.datastore.SETTINGS_DATA_STORE_QUALIFIER
import com.image.upscaler.datastore.writeIntIdentifiable
import com.image.upscaler.intent.InputImageIntentManager
import com.image.upscaler.navigation.ChildComponent
import com.image.upscaler.navigation.RootComponent
import com.image.upscaler.navigation.getViewModel
import com.image.upscaler.shared.model.DataState
import com.image.upscaler.shared.model.InputImage
import com.image.upscaler.shared.model.OutputFormat
import com.image.upscaler.work.RealESRGANWorker
import com.image.upscaler.work.RealESRGANWorkerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.io.File
import java.io.InputStream

class HomePageComponent(
    componentContext: ComponentContext,
    navigation: StackNavigation<RootComponent.Config>
) : ChildComponent<HomePageComponent.ViewModel>(componentContext, navigation) {

    override val viewModel = getViewModel(::ViewModel)

    class ViewModel : ChildComponent.ViewModel(), KoinComponent {

        private val context by inject<Context>()

        private val realESRGANWorkerManager = get<RealESRGANWorkerManager>()
        private val inputImageIntentManager by inject<InputImageIntentManager>()
        private val dataStore by inject<DataStore<Preferences>>(SETTINGS_DATA_STORE_QUALIFIER)
        private val outputFormatPrefKey = intPreferencesKey("output_format")
        private val upscalingModelPrefKey = intPreferencesKey("upscaling_model")

        private val _selectedImageFlow: MutableStateFlow<DataState<InputImage, Unit>?>

        val selectedOutputFormatFlow: MutableStateFlow<OutputFormat>
        val selectedUpscalingModelFlow: MutableStateFlow<UpscalingModel>
        val selectedImageFlow: StateFlow<DataState<InputImage, Unit>?>
        val workProgressFlow = realESRGANWorkerManager.workProgressFlow

        init {
            val inputData = workProgressFlow.value?.first
            if (inputData != null) {
                _selectedImageFlow =
                    MutableStateFlow(DataState.Success(inputData.toInputImage(context)))
                selectedOutputFormatFlow = MutableStateFlow(inputData.outputFormat)
                selectedUpscalingModelFlow = MutableStateFlow(inputData.upscalingModel)
            } else {
                _selectedImageFlow = MutableStateFlow(null)
                val prefs = runBlocking { dataStore.data.first() }
                selectedOutputFormatFlow = MutableStateFlow(
                    OutputFormat.fromId(prefs[outputFormatPrefKey]) ?: OutputFormat.PNG
                )
                selectedUpscalingModelFlow = MutableStateFlow(
                    UpscalingModel.fromId(prefs[upscalingModelPrefKey]) ?: UpscalingModel.X2_PLUS
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                selectedOutputFormatFlow.collect {
                    dataStore.writeIntIdentifiable(outputFormatPrefKey, it)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                selectedUpscalingModelFlow.collect {
                    dataStore.writeIntIdentifiable(upscalingModelPrefKey, it)
                }
            }
            selectedImageFlow = _selectedImageFlow

            viewModelScope.launch {
                inputImageIntentManager.imageUriFlow.collect {
                    when (realESRGANWorkerManager.workProgressFlow.value?.second) {
                        is RealESRGANWorker.Progress.Running -> {}
                        is RealESRGANWorker.Progress.Failed, is RealESRGANWorker.Progress.Success -> {
                            consumeWorkCompleted()
                            clearSelectedImage()
                            loadImage(it)
                        }

                        null -> loadImage(it)
                    }
                }
            }

        }

        fun loadImage(imageUri: Uri) {
            _selectedImageFlow.apply {
                val currentTempImageFile = (value as? DataState.Success)?.data?.tempFile
                tryEmit(DataState.Loading())
                viewModelScope.launch(Dispatchers.IO) {
                    currentTempImageFile?.let { realESRGANWorkerManager.deleteTempImageFile(it) }
                    createInputImage(imageUri)?.let {
                        emit(DataState.Success(it))
                    } ?: emit(DataState.Error(Unit))
                }
            }
        }

        private fun createInputImage(imageUri: Uri): InputImage? {
            val imageFileName = DocumentFile.fromSingleUri(context, imageUri)?.name ?: return null
            val tempImageFile = context.contentResolver.openInputStream(imageUri)?.use {
                copyToTempFile(it)
            } ?: return null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            tempImageFile.inputStream().use {
                BitmapFactory.decodeStream(it, null, options)
            }
            return InputImage(imageFileName, tempImageFile, options.outWidth, options.outHeight)
        }

        private fun copyToTempFile(inputStream: InputStream): File =
            realESRGANWorkerManager.createTempImageFile().apply {
                outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

        fun upscale() = (selectedImageFlow.value as DataState.Success).data.let {
            realESRGANWorkerManager.beginWork(
                RealESRGANWorker.InputData(
                    it.fileName,
                    it.tempFile.name,
                    selectedOutputFormatFlow.value,
                    selectedUpscalingModelFlow.value
                )
            )
        }

        fun cancelWork() {
            realESRGANWorkerManager.cancelWork()
        }

        fun consumeWorkCompleted() {
            realESRGANWorkerManager.clearCurrentWorkProgress()
        }

        fun clearSelectedImage() = _selectedImageFlow.value.let {
            _selectedImageFlow.tryEmit(null)
            if (it is DataState.Success) {
                viewModelScope.launch {
                    realESRGANWorkerManager.deleteTempImageFile(it.data.tempFile)
                }
            }
        }
    }
}

private fun RealESRGANWorker.InputData.toInputImage(context: Context) =
    with(RealESRGANWorkerManager.getTempFile(context, tempFileName)) {
        inputStream().use {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(it, null, options)
            InputImage(originalFileName, this, options.outWidth, options.outHeight)
        }
    }
