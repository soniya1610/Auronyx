# ♻️ EcoVision - AI-Powered E-Waste Analyzer

EcoVision is an AI-powered e-waste analysis system that identifies different types of electronic waste from images and provides an estimated reference scrap value.

The system uses a **MobileNetV2 transfer learning model** for e-waste image classification and a real-source based price dataset for reference scrap value estimation.

---

## 🚀 Features

- 📷 Upload an e-waste image
- 📸 Capture an e-waste image using camera
- 🤖 AI-based e-waste classification
- 📊 Prediction confidence score
- 💰 Estimated reference scrap value
- 🌐 Interactive Streamlit web application
- ♻️ Supports e-waste identification and recycling assistance

---

## 🧠 AI Model

The project uses **MobileNetV2 with Transfer Learning**.

A pretrained MobileNetV2 model is used as the feature extraction base, followed by custom classification layers for identifying e-waste categories.

### Model Architecture

```text
Input Image
      ↓
Image Preprocessing
      ↓
MobileNetV2
      ↓
Global Average Pooling
      ↓
Dense Layer
      ↓
Dropout
      ↓
Softmax
      ↓
E-Waste Category

### Model Performance

**Test Accuracy: 94.33%**

### Supported Categories

- Battery
- Keyboard
- Microwave
- Mobile
- Mouse
- PCB
- Player
- Printer
- Television
- Washing Machine

## 💰 Price Estimation

The system provides reference scrap values based on collected market-source data.

Price units:

- ₹ / kg
- ₹ / piece

Actual prices may vary depending on location, condition and buyer.

## 🛠️ Technologies Used

- Python
- TensorFlow
- Keras
- MobileNetV2
- NumPy
- Pandas
- Scikit-learn
- Pillow
- Streamlit
- Jupyter Notebook

## ▶️ Run the Application

Install the required dependencies:

```bash
pip install -r requirements.txt