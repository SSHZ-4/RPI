package start;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import sp.voice.DataTest;
import test.Media;
import zx.rpc.support.RPC;

public class Server {
	static ByteArrayInputStream bais = null;
	static AudioFormat af = null;
	static AudioInputStream ais = null;
	SourceDataLine sd = null;
	static ByteArrayOutputStream baos = null;

	public static void main(String [] args) throws Exception
	{
		  Server s = new Server();
		  ServerSocket server = new ServerSocket(11348);
		  byte []  b ;
		  while(true)
		  {
			  b = new byte[1000];
			  System.out.println("监听中");
			  Socket socket = server.accept();
			  System.out.println("收到请求");
			  InputStream in = socket.getInputStream();
			  FileOutputStream f =new FileOutputStream("123.wav");
			 while(in.read(b)!=-1)
			 {
				 f.write(b);
			 }
			  f.close();
			  
			  /*
			  Media m = RPC.getProxy(Media.class, "169.254.178.141", 20382);
				byte [] buffer = m.takePhotos();
				FileOutputStream file = new FileOutputStream("f:\\zx.jpg");
				
				file.write(buffer);
				file.flush();
				file.close();
				*/
			//  synchronized(new Object()) {
				  DataTest.read("123.wav");
		//	}
			  
			  System.out.println(b.length+"读的大小");
			  
			  
		/*	  //多线程问题，让其停10秒
			  Thread.sleep(10000);
			  System.out.println("准备发送数据");
			  File file = new File("D:/sunpeng.pcm");
			  byte [] buf =new byte[1000];
			  int len=0;
			  
			  OutputStream outputStream = socket.getOutputStream();
			  
			FileInputStream fis = new FileInputStream(file);
			  while((len=fis.read(buf))!=-1)
		        {
		           outputStream.write(buf);
		        }
			  Thread.sleep(5000);
			  socket.shutdownOutput();		 
			  socket.close();*/
			
		  }
	}
	
	private static String filePathString = null;
	
	//保存录音
		public static String save(byte [] audioData) throws IOException
		{
			 //取得录音输入流
	        af = getAudioFormat();
	        
	        /*
	        ByteArrayOutputStream  baos2 =  new ByteArrayOutputStream();
	        byte[] buff = new byte[1024*100*2];
	        int rc = 0;
	        while ((rc = in.read(buff, 0, 1024*100*2)) > 0) {
	        	baos2.write(buff, 0, rc);
	        }
	       
	        byte audioData[] = baos2.toByteArray();
	        */
	        
	        System.out.println(audioData.length+"接受存储的大小");
	        
	        bais = new ByteArrayInputStream(audioData);
	        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());
	        //定义最终保存的文件名
	        File file = null;
	        //写入文件
	        try {	
	        	//以当前的时间命名录音的名字
	        	//将录音的文件存放到F盘下语音文件夹下
	        	File filePath = new File("c:/语音文件");
	        	if(!filePath.exists())
	        	{//如果文件不存在，则创建该目录
	        		filePath.mkdir();
	        	}
	        	String FileName = System.currentTimeMillis()+".mp3";
	        	file = new File(filePath.getPath()+"/"+FileName);      
	        	
	            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
	            filePathString = FileName;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally{
	        	//关闭流
	        	try {
	        		
	        		if(bais != null)
	        		{
	        			bais.close();
	        		} 
	        		if(ais != null)
	        		{
	        			ais.close();		
	        		}
				} catch (Exception e) {
					e.printStackTrace();
				}   	
	        }
			return filePathString;
		}
	
		//设置AudioFormat的参数
		public static AudioFormat getAudioFormat() 
		{
			//下面注释部分是另外一种音频格式，两者都可以
			AudioFormat.Encoding encoding = AudioFormat.Encoding.
	        PCM_SIGNED ;
			float rate = 8000f;
			int sampleSize = 8;
			String signedString = "unsigned";
			boolean bigEndian = true;
			int channels = 1;
			return new AudioFormat(encoding, rate, sampleSize, channels,
					(sampleSize / 8) * channels, rate, bigEndian);
//			//采样率是每秒播放和录制的样本数
//			float sampleRate = 16000.0F;
//			// 采样率8000,11025,16000,22050,44100
//			//sampleSizeInBits表示每个具有此格式的声音样本中的位数
//			int sampleSizeInBits = 16;
//			// 8,16
//			int channels = 1;
//			// 单声道为1，立体声为2
//			boolean signed = true;
//			// true,false
//			boolean bigEndian = true;
//			// true,false
//			return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
		}
	
	
	
	
	class Play implements Runnable
	{
		//播放baos中的数据即可
		public void run() {
			byte bts[] = new byte[102400*2/3];
			try {
				int cnt;
	            //璇诲彇鏁版嵁鍒扮紦瀛樻暟鎹�
	            while ((cnt = ais.read(bts, 0, bts.length)) != -1) 
	            {
	                if (cnt > 0) 
	                {
	                    //鍐欏叆缂撳瓨鏁版嵁
	                    //灏嗛煶棰戞暟鎹啓鍏ュ埌娣烽鍣�
	                    sd.write(bts, 0, cnt);
	                }
	            }
	           
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				 sd.drain();
		         sd.close();
			}
			
			
		}		
	}
}
