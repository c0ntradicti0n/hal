package record;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.jme3.profile.AppProfiler;

public  class FileVideoRecorder extends AbstractVideoRecorder{
    int current;
    File outDir;
    String formatName = "png";
        
    public FileVideoRecorder(File output) throws IOException {
        super(output);
        if (output.exists() 
            && output.isDirectory() 
            && (0 == output.listFiles().length)){
            // good
        }
        else if (!output.exists()){
            output.mkdir();
        }
        else {
            throw new IOException("argument must be either an empty " + 
                                  "directory or a nonexistent one.");
        }
        this.outDir = output;
    }

    public void record(BufferedImage rawFrame) {
        String name = String.format("%07d.%s" , current++, formatName);
        File target = new File(output, name);
        try {ImageIO.write(rawFrame, formatName, target);}
        catch (IOException e) {e.printStackTrace();}
    }

    public void finish() {}

	@Override
	public void setProfiler(AppProfiler arg0) {
		// TODO Auto-generated method stub
		
	}
}