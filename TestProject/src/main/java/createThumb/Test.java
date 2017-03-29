package createThumb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class Test extends MediaListenerAdapter {
	private static String INPUT_DIR = "E:\\test\\";
	private static String OUTPUT_DIR = "E:\\test\\ss";

	String[] suffixArray = {"wmv","mp4","flv"};

	private int mVideoStreamIndex = -1;
	File[] files = new File(INPUT_DIR).listFiles();
	private int fileIndex = 0;

	public static void main(String[] args) {
		new Test();
	}

	public Test() {

		for (File file : files) {
			if (!Arrays.asList(suffixArray).contains(getSuffix(file.getName()))) {
				fileIndex++;
				continue;
			}
			IMediaReader reader = ToolFactory.makeReader(file.getPath());

			reader.setBufferedImageTypeToGenerate(5);

			reader.addListener(this);
			try {
				while (reader.readPacket() == null && mVideoStreamIndex == -1) {
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
			}
			reader.close();
			mVideoStreamIndex = -1;
			fileIndex++;
		}
	}

	public void onVideoPicture(IVideoPictureEvent event) {
		try {
			if (event.getStreamIndex().intValue() != this.mVideoStreamIndex) {
				if (-1 == this.mVideoStreamIndex) {
					this.mVideoStreamIndex = event.getStreamIndex().intValue();
				} else {
					return;
				}
			}
			BufferedImage sumbImage = reSize3(event.getImage(), 50,50);
//			File outputFile = File.createFile("frame", ".png", new File(OUTPUT_DIR));
			File outputFile = new File(OUTPUT_DIR, "_smb_".concat(files[fileIndex].getName()).concat(".png"));

			ImageIO.write(sumbImage, "png", outputFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getSuffix(String fileName) {
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(point + 1);
	    }
	    return fileName;
	}

	private static BufferedImage reSize3(BufferedImage img, int width, int height) {
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		BufferedImage rescaled = resampleOp.filter(img, null);
		return rescaled;
	}
}