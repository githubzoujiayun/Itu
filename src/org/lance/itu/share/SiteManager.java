package org.lance.itu.share;

import android.content.Context;

public abstract class SiteManager {
	/** ��¼ */
	public abstract void login();

	/** ���ͷ������� lat,lonΪ��γ��λ�� */
	public abstract void share(Context context, String content, String file,
			String lat, String lon);

	/** ���ͽ������(�����쳣���ش����) */
	public abstract void shareResult();

	/** �����û���Ϣ */
	public abstract void getUserInfo(final Context context);

	/** ���ݵ�¼���ˢ�±���token */
	public boolean refreshLocalToken() {
		return true;
	}

	/** ���ݻ�ȡ�ĺ���ˢ�±��غ������ݿ� */
	public boolean refreshLocalFriends() {
		return true;
	}

}
