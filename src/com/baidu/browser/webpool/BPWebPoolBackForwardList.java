package com.baidu.browser.webpool;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BPWebPoolBackForwardList 
 * @Description: ǰ�������б� 
 * @author LEIKANG 
 * @date 2012-12-6 ����2:24:30
 */
public final class BPWebPoolBackForwardList {

	/** ��ǰ�б�λ�� */
	private int mCurIndex = -1;
	/** ǰ�������б����� */
	private List<BPWebPoolBackForwardListItem> mArray;

	/**
	 * Constructor
	 */
	public BPWebPoolBackForwardList() {
		mArray = new ArrayList<BPWebPoolBackForwardListItem>();
	}

	/**
	 * ���һ��Ԫ��
	 * 
	 * @param aItem
	 *            ����ӵ�Ԫ��
	 */
	public void addItem(BPWebPoolBackForwardListItem aItem) {
		if (aItem != null) {
			mArray.add(aItem);
		}
	}

	/**
	 * �滻һ��Ԫ��
	 * 
	 * @param aIndex
	 *            ���滻������
	 * @param aItem
	 *            ���滻��Ԫ��
	 */
	public void setItem(int aIndex, BPWebPoolBackForwardListItem aItem) {
		if (aItem != null && aIndex >= 0 && aIndex < mArray.size()) {
			mArray.set(aIndex, aItem);
		}
	}

	/**
	 * ɾ��һ��Ԫ��
	 * 
	 * @param aIndex
	 *            ��ɾ����Ԫ������
	 */
	public void removeItem(int aIndex) {
		if (aIndex >= 0 && aIndex < mArray.size()) {
			mArray.remove(aIndex);
		}
	}

	/**
	 * ɾ��һ��Ԫ��
	 * 
	 * @param aItem
	 *            ��ɾ����Ԫ��
	 */
	public void removeItem(BPWebPoolBackForwardListItem aItem) {
		if (aItem != null) {
			mArray.remove(aItem);
		}
	}

	/**
	 * ��ȡ��ǰ�б�λ��
	 * 
	 * @return ���ص�ǰ�б�λ��
	 */
	public int getCurIndex() {
		return mCurIndex;
	}

	/**
	 * ��ȡ��ǰ�б�Ԫ��
	 * 
	 * @return ���ص�ǰ�б�Ԫ��
	 */
	public BPWebPoolBackForwardListItem getCurItem() {
		return getItem(mCurIndex);
	}

	/**
	 * ��ȡָ���б�Ԫ��
	 * 
	 * @param aIndex
	 *            ָ���б�Ԫ������
	 * @return ����ָ���б�Ԫ��
	 */
	public BPWebPoolBackForwardListItem getItem(int aIndex) {
		if (aIndex >= 0 && aIndex < mArray.size()) {
			return mArray.get(aIndex);
		} else {
			throw new ArrayIndexOutOfBoundsException(aIndex);
		}
	}

	/**
	 * ��ȡָ���б�Ԫ�ص�����
	 * 
	 * @param aItem
	 *            ָ���б�Ԫ��
	 * @return ָ���б�Ԫ�ص�����
	 */
	public int getItemIndex(BPWebPoolBackForwardListItem aItem) {
		return mArray.indexOf(aItem);
	}

	/**
	 * ��ȡ�б�Ԫ������
	 * 
	 * @return �б�Ԫ������
	 */
	public int getSize() {
		return mArray.size();
	}

	/**
	 * �Ƿ��ܹ�ǰ������ָ������
	 * 
	 * @param aSteps
	 *            The negative or positive number of steps to move the history.
	 * @return �Ƿ��ܹ�ǰ�����ߺ���ָ���Ĳ���
	 */
	public boolean canGoBackOrForward(int aSteps) {
		int newIndex = mCurIndex + aSteps;
		return newIndex >= 0 && newIndex < mArray.size();
	}

	/**
	 * �Ƿ���Ժ���
	 * 
	 * @return �ܹ����˷���true�����򷵻�false
	 */
	public boolean canGoBack() {
		return canGoBackOrForward(-1);
	}

	/**
	 * �Ƿ��ܹ�ǰ��
	 * 
	 * @return �ܹ�ǰ������true�����򷵻�false
	 */
	public boolean canGoForward() {
		return canGoBackOrForward(1);
	}

	/**
	 * ǰ������ָ������
	 * 
	 * @param aSteps
	 *            The negative or positive number of steps to move the history.
	 */
	public void goBackOrForward(int aSteps) {
		if (canGoBackOrForward(aSteps)) {
			mCurIndex += aSteps;
		}
	}

	/**
	 * ����
	 */
	public void goBack() {
		if (canGoBack()) {
			mCurIndex -= 1;
		}
	}

	/**
	 * ǰ��
	 */
	public void goForward() {
		if (canGoForward()) {
			mCurIndex += 1;
		}
	}

	/**
	 * ���б���
	 */
	public void goToFirst() {
		mCurIndex = 0;
	}

	/**
	 * ���б�β
	 */
	public void goToLast() {
		mCurIndex = mArray.size() - 1;
	}

	/**
	 * ����б�����
	 */
	public void clear() {
		mArray.clear();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getSize() + "\n");
		for (BPWebPoolBackForwardListItem item : mArray) {
			if (item != null) {
				sb.append(item.getUrl());
				sb.append(", ");
				sb.append(item.getWebView());
				sb.append(", ");
				sb.append(item.getIndex());
				sb.append(", ");
				sb.append(item.getLoadStatus());
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
