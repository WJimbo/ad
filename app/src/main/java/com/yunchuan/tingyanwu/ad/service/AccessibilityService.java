package com.yunchuan.tingyanwu.ad.service;


import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    private static final String TAG = "AutoInstallService";
    private static String PACKAGE_INSTALLER = "com.android.packageinstaller";

    public AccessibilityService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        /*
         * 回调方法，当事件发生时会从这里进入，在这里判断需要捕获的内容，
         * 可通过下面这句log将所有事件详情打印出来，分析决定怎么过滤。
         */
        //log(event.toString());
        if (event.getSource() == null) {
            log("<null> event source");
            return;
        }
        int eventType = event.getEventType();
        /*
         * 在弹出安装界面时会发生 TYPE_WINDOW_STATE_CHANGED 事件，其属主
         * 是系统安装器com.android.packageinstaller
         */
        if (true || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && event.getPackageName().equals(PACKAGE_INSTALLER)) {
            boolean r = performInstallation(event);
            log("Action Perform: " + r);
        }

    }

    @Override
    public void onInterrupt() {
        log("AutoInstallServiceInterrupted");
    }

    private void log(String s) {
        Log.d(TAG, s);
    }

    private boolean performInstallation(AccessibilityEvent event) {
        List<AccessibilityNodeInfo> nodeInfoList;
        /*
         * 有的手机会弹2次，有的只弹一次，在替换安装时会出现确定按钮，
         * 为了大而全，下面定义了比较多的内容，可按需增减。
         */
        String[] labels = new String[]{"确定", "安装", "下一步", "完成", "Open", "Install"};
        for (String label : labels) {
//            nodeInfoList = event.getSource().findAccessibilityNodeInfosByText(label);
            if (getRootInActiveWindow()!=null){
            nodeInfoList=getRootInActiveWindow().findAccessibilityNodeInfosByText(label);

            if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
                boolean performed = performClick(nodeInfoList);
                if (performed) return true;
            }}
        }
        return false;
    }

    private boolean performClick(List<AccessibilityNodeInfo> nodeInfoList) {
        for (AccessibilityNodeInfo node : nodeInfoList) {
            /*
             * 这里还可以根据node的类名来过滤，大多数是button类，这里也是为了大而全，
             * 判断只要是可点击的是可用的就点。
             */
            if (node.getText()!=null)
            log(node.getText().toString()+"________node text");
            if (node.isClickable() && node.isEnabled()) {
                return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        return false;
    }
}

//
//public class AccessibilityService extends android.accessibilityservice.AccessibilityService {
//
//    Map<Integer, Boolean> handledMap = new HashMap<>();
//
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
//        AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();
//        if (nodeInfo != null) {
//            int eventType = accessibilityEvent.getEventType();
//            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
//                    eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//                if (true || handledMap.get(accessibilityEvent.getWindowId()) == null) {
//                    boolean handled = iterateNodesAndHandle(nodeInfo);
//                    if (handled) {
//                        handledMap.put(accessibilityEvent.getWindowId(), true);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onInterrupt() {
//
//    }
//
//    private boolean iterateNodesAndHandle(AccessibilityNodeInfo nodeInfo) {
//        if (nodeInfo != null) {
//            int childCount = nodeInfo.getChildCount();
//            if ("android.widget.Button".equals(nodeInfo.getClassName())) {
//                String nodeContent = nodeInfo.getText().toString();
//                Log.d("TAG", "content is " + nodeContent);
//                if ("安装".equals(nodeContent)
//                        || "完成".equals(nodeContent)
//                        || "确定".equals(nodeContent)  || "Install".equals(nodeContent)  || "Open".equals(nodeContent) ) {
//                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    return true;
//                }
//            } else if ("android.widget.ScrollView".equals(nodeInfo.getClassName())) {
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
//            }
//            for (int i = 0; i < childCount; i++) {
//                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
//                if (iterateNodesAndHandle(childNodeInfo)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//}
