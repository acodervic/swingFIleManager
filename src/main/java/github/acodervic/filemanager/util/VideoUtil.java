package github.acodervic.filemanager.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.function.Function;

public class VideoUtil {
    public static boolean getVideoFirstImg(File videoFile,File pngFile)throws Exception {
        Frame frame = null;
       //构造器支持InputStream，可以直接传MultipartFile.getInputStream()
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(videoFile);
        //开始播放
        fFmpegFrameGrabber.start();
        //获取视频总帧数
        int ftp = fFmpegFrameGrabber.getLengthInFrames();
        //指定第几帧
        fFmpegFrameGrabber.setFrameNumber(5);
        //获取指定第几帧的图片
        frame = fFmpegFrameGrabber.grabImage();
        //文件绝对路径+名字
        ImageIO.write(FrameToBufferedImage(frame), "png", pngFile);
        return true;
    }

    public static boolean getVideoLastImg(File videoFile,File pngFile)throws Exception {
        Frame frame = null;
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(videoFile);
        //开始播放
        fFmpegFrameGrabber.start();
        //获取视频总帧数
        int ftp = fFmpegFrameGrabber.getLengthInFrames();
        //指定第几帧
        fFmpegFrameGrabber.setFrameNumber(ftp - 1);
        //获取指定第几帧的图片
        frame = fFmpegFrameGrabber.grabImage();
        ImageIO.write(FrameToBufferedImage(frame), "png", pngFile);
        return true;
    }

    public static boolean getVideoMiddleImg(File videoFile,File pngFile)throws Exception {
        Frame frame = null;
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(videoFile);
        //开始播放
        fFmpegFrameGrabber.start();
        //获取视频总帧数
        int ftp = fFmpegFrameGrabber.getLengthInFrames();
        //指定第几帧
        fFmpegFrameGrabber.setFrameNumber(ftp/2);
        //获取指定第几帧的图片
        frame = fFmpegFrameGrabber.grabImage();
        ImageIO.write(FrameToBufferedImage(frame), "png", pngFile);
        return true;
    }

    public static boolean getVideoImgByFrameIndex(File videoFile,File pngFile,Function<Integer,Integer>  frameCount)throws Exception {
        Frame frame = null;
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(videoFile);
        //开始播放
        fFmpegFrameGrabber.start();
        //获取视频总帧数
        int ftp = fFmpegFrameGrabber.getLengthInFrames();
        //指定第几帧
        fFmpegFrameGrabber.setFrameNumber(frameCount.apply(ftp));
        //获取指定第几帧的图片
        frame = fFmpegFrameGrabber.grabImage();
        ImageIO.write(FrameToBufferedImage(frame), "png", pngFile);
        return true;
    }


    public static BufferedImage FrameToBufferedImage(Frame frame) {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

    public static void main(String[] args) throws Exception{
        String videoFileName = "/home/w/Music/video_2022-03-29_22-18-44.mp4";
        System.out.println(getVideoImgByFrameIndex(new File(videoFileName),new File("123.png"),f  ->{
            return f-203;
        }));
    }

}