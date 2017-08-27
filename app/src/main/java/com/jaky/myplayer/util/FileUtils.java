package com.jaky.myplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.jaky.myplayer.config.Constant;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FileUtils {

	public static String TAG_FILENAME = "fileName";
	public static String TAG_FILEPATH = "filePath";
	public static String TAG_FILESIZE = "fileSize";
	private static final HashSet<String> mHashVideo;
	private static final HashSet<String> mHashAudio;
	private static final double KB = 1024.0;
	private static final double MB = KB * KB;
	private static final double GB = KB * KB * KB;

	static {
		mHashVideo = new HashSet<String>(Arrays.asList(Constant.VIDEO_EXTENSIONS));
		mHashAudio = new HashSet<String>(Arrays.asList(Constant.AUDIO_EXTENSIONS));
	}

	public static boolean isVideoOrAudio(File f) {
		final String ext = getFileExtension(f);
		return mHashVideo.contains(ext) || mHashAudio.contains(ext);
	}

	public static boolean isVideoOrAudio(String f) {
		final String ext = getUrlExtension(f);
		return mHashVideo.contains(ext) || mHashAudio.contains(ext);
	}

	public static boolean isVideo(File f) {
		final String ext = getFileExtension(f);
		return mHashVideo.contains(ext);
	}

	public static String getFileExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}

	public static String getUrlFileName(String url) {
		int slashIndex = url.lastIndexOf('/');
		int dotIndex = url.lastIndexOf('.');
		String filenameWithoutExtension;
		if (dotIndex == -1) {
			filenameWithoutExtension = url.substring(slashIndex + 1);
		} else {
			filenameWithoutExtension = url.substring(slashIndex + 1, dotIndex);
		}
		return filenameWithoutExtension;
	}

	public static String getUrlExtension(String url) {
		if (!StringUtils.isEmpty(url)) {
			int i = url.lastIndexOf('.');
			if (i > 0 && i < url.length() - 1) {
				return url.substring(i + 1).toLowerCase();
			}
		}
		return "";
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static String showFileSize(long size) {
		String fileSize;
		if (size < KB)
			fileSize = size + "B";
		else if (size < MB)
			fileSize = String.format("%.1f", size / KB) + "KB";
		else if (size < GB)
			fileSize = String.format("%.1f", size / MB) + "MB";
		else
			fileSize = String.format("%.1f", size / GB) + "GB";

		return fileSize;
	}

	public static String showFileAvailable() {
		String result = "";
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			StatFs sf = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			return showFileSize(availCount * blockSize) + " / "
					+ showFileSize(blockSize * blockCount);
		}
		return result;
	}

	public static boolean createIfNoExists(String path) {
		File file = new File(path);
		boolean mk = false;
		if (!file.exists()) {
			mk = file.mkdirs();
		}
		return mk;
	}

	private static HashMap<String, String> mMimeType = new HashMap<String, String>();
	static {
		mMimeType.put("M1V", "video/mpeg");
		mMimeType.put("MP2", "video/mpeg");
		mMimeType.put("MPE", "video/mpeg");
		mMimeType.put("MPG", "video/mpeg");
		mMimeType.put("MPEG", "video/mpeg");
		mMimeType.put("MP4", "video/mp4");
		mMimeType.put("M4V", "video/mp4");
		mMimeType.put("3GP", "video/3gpp");
		mMimeType.put("3GPP", "video/3gpp");
		mMimeType.put("3G2", "video/3gpp2");
		mMimeType.put("3GPP2", "video/3gpp2");
		mMimeType.put("MKV", "video/x-matroska");
		mMimeType.put("WEBM", "video/x-matroska");
		mMimeType.put("MTS", "video/mp2ts");
		mMimeType.put("TS", "video/mp2ts");
		mMimeType.put("TP", "video/mp2ts");
		mMimeType.put("WMV", "video/x-ms-wmv");
		mMimeType.put("ASF", "video/x-ms-asf");
		mMimeType.put("ASX", "video/x-ms-asf");
		mMimeType.put("FLV", "video/x-flv");
		mMimeType.put("MOV", "video/quicktime");
		mMimeType.put("QT", "video/quicktime");
		mMimeType.put("RM", "video/x-pn-realvideo");
		mMimeType.put("RMVB", "video/x-pn-realvideo");
		mMimeType.put("VOB", "video/dvd");
		mMimeType.put("DAT", "video/dvd");
		mMimeType.put("AVI", "video/x-divx");
		mMimeType.put("OGV", "video/ogg");
		mMimeType.put("OGG", "video/ogg");
		mMimeType.put("VIV", "video/vnd.vivo");
		mMimeType.put("VIVO", "video/vnd.vivo");
		mMimeType.put("WTV", "video/wtv");
		mMimeType.put("AVS", "video/avs-video");
		mMimeType.put("SWF", "video/x-shockwave-flash");
		mMimeType.put("YUV", "video/x-raw-yuv");
	}

	public static String getMimeType(String path) {
		int lastDot = path.lastIndexOf(".");
		if (lastDot < 0)
			return null;

		return mMimeType.get(path.substring(lastDot + 1).toUpperCase());
	}

	public static String getExternalStorageDirectory() {
		Map<String, String> map = System.getenv();
		String[] values = new String[map.values().size()];
		map.values().toArray(values);
		String path = values[values.length - 1];
		Log.e("nmbb", "FileUtils.getExternalStorageDirectory : " + path);
		if (path.startsWith("/mnt/")
				&& !Environment.getExternalStorageDirectory().getAbsolutePath()
						.equals(path))
			return path;
		else
			return null;
	}

	public static String getCanonical(File f) {
		if (f == null)
			return null;

		try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			return f.getAbsolutePath();
		}
	}
	
	public static boolean sdAvailable() {
		return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
				.getExternalStorageState())
				|| Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState());
	}

	public static void closeQuietly(Cursor cursor) {
		try {
			if (cursor != null)
				cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null)
				closeable.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readContentOfFile(File fileForRead) {
		FileInputStream in = null;
		InputStreamReader reader = null;
		BufferedReader breader = null;
		try {
			in = new FileInputStream(fileForRead);
			reader = new InputStreamReader(in, "utf-8");
			breader = new BufferedReader(reader);

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = breader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeQuietly(breader);
			closeQuietly(reader);
			closeQuietly(in);
		}
		return null;
	}

	public static List<String> readStringListOfFile(File fileForRead) {
		FileInputStream in = null;
		InputStreamReader reader = null;
		BufferedReader breader = null;
		List<String> list = new ArrayList<String>();
		try {
			in = new FileInputStream(fileForRead);
			reader = new InputStreamReader(in, "utf-8");
			breader = new BufferedReader(reader);

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = breader.readLine()) != null) {
				list.add(line);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeQuietly(breader);
			closeQuietly(reader);
			closeQuietly(in);
		}
		return null;
	}

	public static List<String> readStringListOfFileWithEncoding(File fileForRead, final String encoding) {
		FileInputStream in = null;
		InputStreamReader reader = null;
		BufferedReader breader = null;
		List<String> list = new ArrayList<String>();
		try {
			in = new FileInputStream(fileForRead);
			reader = new InputStreamReader(in, encoding);
			breader = new BufferedReader(reader);

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = breader.readLine()) != null) {
				list.add(line);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeQuietly(breader);
			closeQuietly(reader);
			closeQuietly(in);
		}
		return null;
	}


	public static boolean saveContentToFile(String content, File fileForSave) {
		boolean succeed = true;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileForSave);
			out.write(content.getBytes("utf-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			succeed = false;
		} finally {
			closeQuietly(out);
		}
		return succeed;
	}

	public static boolean deleteFile(final String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
			return true;
		}
		return false;
	}

	public static boolean makeDirectory(final String path){
		File destDir = new File(path);
		if(!destDir.exists()){
			return destDir.mkdirs();
		}
		return false;
	}

	public static String readableFileSize(long size) {
		if (size <= 0) return "0";
		final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static long getFileSize(File file) {
		if (file.isDirectory()) {
			long size = 0;
			try {
				File[] fileList = file.listFiles();
				for (File temp : fileList) {
					if (temp.isDirectory()) {
						size = size + getFileSize(temp);

					} else {
						size = size + temp.length();

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return size;
		} else if (file.exists() && file.isFile()) {
			return file.length();
		}
		return -1;
	}

	//Use NioMethod(FileChannel) to Copy File To Specific Directory
	public static void copyToDestinationDirectory(File sourceFile, String destinationFolder, String resultFileName) throws IOException {
		File dir = new File(destinationFolder);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File targetFile = new File(dir.getAbsolutePath() + resultFileName);
		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}
		String folderCommand = "chmod 777 " + dir.getAbsolutePath();
		String fileCommand = "chmod 777 " + targetFile.getAbsolutePath();
		Runtime runtime = Runtime.getRuntime();
		java.lang.Process proc = runtime.exec(folderCommand);
		proc = runtime.exec(fileCommand);
		FileChannel in;
		FileChannel out;
		FileInputStream inStream;
		FileOutputStream outStream;
		inStream = new FileInputStream(sourceFile);
		outStream = new FileOutputStream(targetFile);
		in = inStream.getChannel();
		out = outStream.getChannel();
		in.transferTo(0, in.size(), out);
		inStream.close();
		in.close();
		outStream.close();
		out.close();
	}

	/*
    *  searchFile with keyWord
    *  @String keyword
    *  @File path
    */
	public static void searchFile(String keyword, File path,
								  ArrayList<HashMap<String, String>> resultList,
								  boolean checkExtension, String extensionKey) {
		File[] files = path.listFiles();
		if (files.length > 0) {
			for (File file : files) {
				if (file.isDirectory()) {
					if (file.canRead()) {
						searchFile(keyword, file, resultList, checkExtension, extensionKey);
					}
				} else {
					if (!file.getPath().contains("/.") &&
							(file.getName().contains(keyword) ||
									file.getName().contains(keyword.toUpperCase()))) {
						HashMap<String, String> resultItem = new HashMap<String, String>();
						if (checkExtension) {
							if (getFileExtension(file).equalsIgnoreCase(extensionKey)) {
								resultItem.put(TAG_FILENAME, file.getName());
								resultItem.put(TAG_FILEPATH, file.getPath());
								resultItem.put(TAG_FILESIZE, Long.toString(file.length()));
							}
						} else {
							resultItem.put(TAG_FILENAME, file.getName());
							resultItem.put(TAG_FILEPATH, file.getPath());
							resultItem.put(TAG_FILEPATH, Long.toString(file.length()));
						}
						resultList.add(resultItem);
					}
				}
			}
		}
	}

	public static boolean deleteFile(File file) {
		return file.exists() && file.isFile() && file.delete();
	}

	public static boolean deleteAllFilesOfDir(File path) {
		if (!path.exists())
			return false;
		if (path.isFile()) {
			return path.delete();
		}
		File[] files = path.listFiles();
		for (File file : files) {
			deleteAllFilesOfDir(file);
		}
		return path.delete();
	}


	public static boolean exists(final String path) {
		File file = new File(path);
		return file.exists();
	}

	public static String getRealFilePathFromUri(Context context, Uri uri) {
		String filePath = null;
		if (uri != null) {
			if ("content".equals(uri.getScheme())) {
				Cursor cursor = context.getContentResolver().query(uri, new String[]{
						android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
				cursor.moveToFirst();
				filePath = cursor.getString(0);
				cursor.close();
			} else {
				filePath = uri.getPath();
			}
		}
		return filePath;
	}

	public static String getFileExtension(String fileName) {
		if (StringUtils.isNullOrEmpty(fileName)) {
			return "";
		}
		int dotPosition = fileName.lastIndexOf('.');
		if (dotPosition >= 0) {
			return fileName.substring(dotPosition + 1).toLowerCase(Locale.getDefault());
		}

		return "";
	}
}
