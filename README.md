
Parallel image processing application

GENERAL:
  Platform:           Android
  Reccomended SDK:    2.2 Froyo(API level 8)
  
DESCRIPTION:
  Android applicatin that recives a immage from the phones gallery and transforms it to a grayscale level 
    using the formula: grayPixel = 0.2989 * R + 0.587 * G + 0.114 * B
  Application does the grayscale transform using multiple treads, that can be configured in the code.
  
PROFETCT FILES:
  src
    ro.aii.pub.ppam
      MainActivity.java             Main application class
  res
    layout
      activity_main.xml             Standard Android XML layout file
      
CODE:
  Parallel processing:
    The selected immage is split into a configured number of parts defined by the numberOfThreads variable 
      located in the MainActiviry class and then each part is processed on a separate thread. By doing this 
      implementation teh immage will be processed much faster than doing everything on one thread.
    Due to the fact that the processing is done in the background the application will not block durring the
      processing
  
