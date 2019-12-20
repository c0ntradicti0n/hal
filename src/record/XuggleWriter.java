package record;

//package com.aurellem.capture.video;

//// import java.awt.image.BufferedImage;
//// import java.io.File;
//// import java.io.IOException;
//// import java.util.concurrent.TimeUnit;

//// import com.xuggle.mediatool.IMediaWriter;
//// import com.xuggle.mediatool.ToolFactory;
//// import com.xuggle.xuggler.IRational;


///**
//* Handles writing video files using Xuggle.
//*
//* @author Robert McIntyre
//*
//*/

//public class XuggleVideoRecorder extends AbstractVideoRecorder{

 
////   IMediaWriter writer;
////   BufferedImage frame;
////   int videoChannel = 0;
////   long currentTimeStamp = 0;
////   boolean videoReady = false;
 
     
//   public XuggleVideoRecorder(File output) 
//           throws IOException {super(output);}
     
////   public void initVideo(){
//   // this.frame = new BufferedImage(
//   //                             width, height,
//   //                             BufferedImage.TYPE_3BYTE_BGR);
//   // this.writer = ToolFactory.makeWriter(this.targetFileName);
//   // writer.addVideoStream(videoChannel, 
//   //                    0, IRational.make(fps), 
//   //                    width, height);
//   // this.videoReady = true;
////   }
     
////   public void record(BufferedImage rawFrame) {
//   // if (!this.videoReady){initVideo();}
//   // // convert the Image into the form that Xuggle likes.
//   // this.frame.getGraphics().drawImage(rawFrame, 0, 0, null);
//   // writer.encodeVideo(videoChannel, 
//   //                 frame,
//   //                 currentTimeStamp, TimeUnit.NANOSECONDS);
             
//   // currentTimeStamp += (long) (1000000000.0 / fps);
////   }

////   public void finish() {
//   // writer.close();
////   }
 
     
//}