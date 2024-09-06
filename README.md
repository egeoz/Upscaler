# Upscaler

**Sharpen your low-resolution pictures with the power of AI upscaling**<br/><br/>
Upscaler is a neural network based image upscaling application for Android built with
the [MNN deep learning framework](https://github.com/alibaba/MNN)
and [Real-ESRGAN](https://github.com/xinntao/Real-ESRGAN).<br/><br/>

The input image is processed in tiles on the device GPU, using a pre-trained Real-ESRGAN model. The
tiles are then merged into the final high-resolution image. This application requires Vulkan or
OpenCL support and Android 7 or above

Or get the latest APK from
the [Releases Section](https://github.com/egeoz/Upscaler/releases/latest).

## 📊 Benchmarks

Results on Qualcomm Snapdragon 855 (Vulkan)
| Mode | Input resolution | Output resolution | Execution time |
| ------------- | ---------------- | ----------------- | ----------------- |
| 4x (generic)  | 1920x1080 | 3840x2160 | 3 minutes |
| 16x (generic) | 1920x1080 | 7680x4320 | 11 minutes |
| 16x (drawing) | 1920x1080 | 7680x4320 | 3 mins 42 seconds |

## 📚 TODO

- Batch processing

## 📝 Credits

- Pre-trained models and original implementation
  from [Real-ESRGAN](https://github.com/xinntao/Real-ESRGAN)
- Original code from [Lucchetto/SuperImage](https://github.com/Lucchetto/SuperImage)

## ⚖️ License

Upscaler is licensed under
the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.html)
