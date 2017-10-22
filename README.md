# Optical Character Recognition
Using Supervised Learning to Recognise Handwriting

![alt tag](https://github.com/vasan10591/OCR/blob/master/img/OCR.png?raw=true)

This project uses an Artificial Neural Network ([ANN](http://pages.cs.wisc.edu/~bolo/shipyard/neural/local.html)) to identify handwritten numbers (from 0-9). This project was coded in java using JFrames. Click on the ProjectData folder above for source code!

To demo the application, download the jar and the Weights.txt files above. When first starting the application, a file picker window should appear. Navigate to the location where you saved the Weights.txt file and choose that file to begin.

Check out other interesting projects on my [website](https://vasan10591.github.io/BitesizeAI/WebsiteDat/index.html)!

# Controling the Program

Using mouse, draw on the white window within application.

**Query Neural Net** - Press to run character recognition. Neural Network prediction/output will appear in upper right hand text box.

**Clear** - Press to clear drawing window.

**Train** - Draw number within window and type in matching answer in upper right hand text box (i.e. draw 3 and type 3). Pressing *Train* will retrain the Neural Network with these inputs thereby improving it.

**Save New** - Save new Neural Net weights (after retraining through the *Train* button) to the Weights.txt file.

**Reverse** - Type in number from 0-9 in upper right hand text box before pressing this button. This generates a depiction of how the Neural Net interprets the types number. User will be prompted to save image through file picker.
