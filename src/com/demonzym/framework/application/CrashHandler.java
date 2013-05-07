package com.demonzym.framework.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
*
*
* UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。 如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框
* 实现该接口并注册为程序中的默认未捕获异常处理 这样当未捕获异常发生时，就可以做些异常处理操作 例如：收集异常信息，发送错误报告 等。
*
* UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
*/
public class CrashHandler implements UncaughtExceptionHandler {
	/** Debug Log Tag */
	public static final String TAG = "CrashHandler";
	/** �Ƿ�����־���, ��Debug״̬�¿���, ��Release״̬�¹ر�������������� */
	public static final boolean DEBUG = true;
	/** CrashHandlerʵ�� */
	private static CrashHandler INSTANCE;
	/** �����Context���� */
	private Context mContext;
	/** ϵͳĬ�ϵ�UncaughtException������ */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	/** ʹ��Properties�������豸����Ϣ�ʹ����ջ��Ϣ */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** ���󱨸��ļ�����չ�� */
	private static final String CRASH_REPORTER_EXTENSION = ".cr";

	/** ��ֻ֤��һ��CrashHandlerʵ�� */
	private CrashHandler() {
	}

	/** ��ȡCrashHandlerʵ�� ,����ģʽ */
	public static CrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	/**
	 * ��ʼ��,ע��Context����, ��ȡϵͳĬ�ϵ�UncaughtException������, ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * ��UncaughtException����ʱ��ת��ú���������
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// ����û�û�д�������ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleepһ���������
			// �����߳�ֹͣһ����Ϊ����ʾToast��Ϣ���û���Ȼ��Kill����
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error : ", e);
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

	/**
	 * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����. �����߿��Ը���Լ���������Զ����쳣�����߼�
	 * 
	 * @param ex
	 * @return true:������˸��쳣��Ϣ;���򷵻�false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		// ʹ��Toast����ʾ�쳣��Ϣ
		new Thread() {
			@Override
			public void run() {
				// Toast ��ʾ��Ҫ������һ���̵߳���Ϣ������
				Looper.prepare();
				Toast.makeText(mContext, "���������:" + msg, Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}
		}.start();
		// �ռ��豸��Ϣ
		collectCrashDeviceInfo(mContext);
		// ������󱨸��ļ�
		String crashFileName = saveCrashInfoToFile(ex);
		// ���ʹ��󱨸浽������
		sendCrashReportsToServer(mContext);
		return true;
	}

	/**
	 * �ռ�����������豸��Ϣ
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			// Class for retrieving various kinds of information related to the
			// application packages that are currently installed on the device.
			// You can find this class through getPackageManager().
			PackageManager pm = ctx.getPackageManager();
			// getPackageInfo(String packageName, int flags)
			// Retrieve overall information about an application package that is
			// installed on the system.
			// public static final int GET_ACTIVITIES
			// Since: API Level 1 PackageInfo flag: return information about
			// activities in the package in activities.
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				// public String versionName The version name of this package,
				// as specified by the <manifest> tag's versionName attribute.
				mDeviceCrashInfo.put(VERSION_NAME,
						pi.versionName == null ? "not set" : pi.versionName);
				// public int versionCode The version number of this package,
				// as specified by the <manifest> tag's versionCode attribute.
				mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// ʹ�÷������ռ��豸��Ϣ.��Build���а�����豸��Ϣ,
		// ����: ϵͳ�汾��,�豸����� �Ȱ�����Գ����������Ϣ
		// ���� Field �����һ�����飬��Щ����ӳ�� Class �������ʾ�����ӿ��������������ֶ�
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				// setAccessible(boolean flag)
				// ���˶���� accessible ��־����Ϊָʾ�Ĳ���ֵ��
				// ͨ������Accessible����Ϊtrue,���ܶ�˽�б������з��ʣ���Ȼ��õ�һ��IllegalAccessException���쳣
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), field.get(null));
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}
		}
	}

	/**
	 * ���������Ϣ���ļ���
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		// printStackTrace(PrintWriter s)
		// ���� throwable ����׷�������ָ���� PrintWriter
		ex.printStackTrace(printWriter);

		// getCause() ���ش� throwable �� cause����� cause �����ڻ�δ֪���򷵻� null��
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		// toString() ���ַ����ʽ���ظû�����ĵ�ǰֵ��
		String result = info.toString();
		printWriter.close();
		mDeviceCrashInfo.put(STACK_TRACE, result);

		try {
			long timestamp = System.currentTimeMillis();
			String fileName = "crash-" + timestamp + CRASH_REPORTER_EXTENSION;
			// �����ļ�
			FileOutputStream trace = mContext.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			mDeviceCrashInfo.store(trace, "");
			trace.flush();
			trace.close();
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
		}
		return null;
	}

	/**
	 * �Ѵ��󱨸淢�͸������,���²���ĺ���ǰû���͵�.
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));

			for (String fileName : sortedFiles) {
				File cr = new File(ctx.getFilesDir(), fileName);
				postReport(cr);
				cr.delete();// ɾ���ѷ��͵ı���
			}
		}
	}

	/**
	 * ��ȡ���󱨸��ļ���
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		// ʵ��FilenameFilter�ӿڵ���ʵ������ڹ������ļ���
		FilenameFilter filter = new FilenameFilter() {
			// accept(File dir, String name)
			// ����ָ���ļ��Ƿ�Ӧ�ð���ĳһ�ļ��б��С�
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		// list(FilenameFilter filter)
		// ����һ���ַ����飬��Щ�ַ�ָ���˳���·�����ʾ��Ŀ¼������ָ�����������ļ���Ŀ¼
		return filesDir.list(filter);
	}

	private void postReport(File file) {
		// TODO ʹ��HTTP Post ���ʹ��󱨸浽������
	}

	/**
	 * �ڳ�������ʱ��, ���Ե��øú�����������ǰû�з��͵ı���
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}
}