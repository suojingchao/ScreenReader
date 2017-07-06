package com.example.cat.accessibilityservicetest;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.ArraySet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.Set;

/**
 * Created by cat on 2017/5/31.
 */

public class MyAccessibilityService extends AccessibilityService {

    private static final int MASK_TYPE = AccessibilityEvent.TYPE_VIEW_HOVER_ENTER | AccessibilityEvent.TYPE_VIEW_CLICKED;
    private static final int MASK_ACCEPTED_EVENT_TYPES =
            AccessibilityEventCompat.TYPE_VIEW_ACCESSIBILITY_FOCUSED
                    | AccessibilityEventCompat.TYPE_VIEW_HOVER_ENTER;


    public static boolean supportsWebActions(AccessibilityNodeInfoCompat node) {
            if (node != null) {
                final int supportedActions = node.getActions();
                int[] actions = new int[2];
                actions[0] = AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT;
                actions[1] = AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT;
                for (int action : actions) {
                    if ((supportedActions & action) == action) {
                        return true;
                    }
                }
            }

            return false;
    }

    private static boolean isNodeFromFirefox(AccessibilityNodeInfoCompat node) {
        if (node == null) {
            return false;
        }

        final String packageName = node.getPackageName() != null ?
                node.getPackageName().toString() : "";
        return packageName.startsWith("org.mozilla.");
    }

    public static boolean hasLegacyWebContent(AccessibilityNodeInfoCompat node) {
        if (node == null) {
            return false;
        }

        // TODO: Need better checking for native versus legacy web content.
        // Right now Firefox is accidentally treated as legacy web content using the current
        // detection routines; the `isNodeFromFirefox` check blacklists any Firefox that supports
        // the native web actions from being treated as "legacy" content.
        // Once we have resolved this issue, remove the `isNodeFromFirefox` disjunct from the check.
        if (!supportsWebActions(node) || isNodeFromFirefox(node)) {
            return false;
        }

        // ChromeVox does not have sub elements, so if the parent element also has web content
        // this cannot be ChromeVox.
        AccessibilityNodeInfoCompat parent = node.getParent();
        if (supportsWebActions(parent)) {
            if (parent != null) {
                parent.recycle();
            }

            return false;
        }

        if (parent != null) {
            parent.recycle();
        }

        // ChromeVox never has child elements
        return node.getChildCount() == 0;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        final AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(accessibilityEvent);
        final AccessibilityNodeInfoCompat source = record.getSource();

        //int flag = accessibilityEvent.getEventType();
        Log.d("SJC", "onAccessibilityEvent-" + accessibilityEvent.getEventType());
        if ((accessibilityEvent.getEventType() & MASK_ACCEPTED_EVENT_TYPES) != 0 && hasLegacyWebContent(source)) {
            Log.d("SJC", "onAccessibilityEvent web content " + accessibilityEvent.getEventType() + source.toString());
        } else if ((accessibilityEvent.getEventType() & AccessibilityEvent.TYPE_VIEW_CLICKED) != 0) {
            Log.d("SJC", "onAccessibilityEvent onclick " + accessibilityEvent.getEventType() + source.toString());
        }


        /*StringBuffer sb = new StringBuffer();
        if (accessibilityEvent.getText() != null) {
            for (CharSequence ch : accessibilityEvent.getText()) {
                sb.append(ch).append(", ");
            }
        }*/
        //Log.d("SJC", "onAccessibilityEvent " + accessibilityEvent.getClassName() + " " + flag + " " + sb.toString() + " contentDes:" + (accessibilityEvent.getSource() == null ? "" : accessibilityEvent.getSource().getContentDescription()));
        //if ((flag & MASK_ACCEPTED_EVENT_TYPES) != 0) {
            //AccessibilityNodeInfo source = accessibilityEvent.getSource();
            //dumpWindow(source);
            //Log.d("SJC", "ON_ACCESSIBILITY_EVENT " + " EventType=" + flag + " PackageName=" + source.getPackageName() + " ClassName=" + source.getClassName() + " Text=" + source.getContentDescription() + " ContentText=" + source.getContentDescription());
            //dumpWindow(source);
        //}

        /*switch (flag) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                //Log.d("SJC", "TYPE_VIEW_CLICKED:" + accessibilityEvent.toString());


                //dumpWindow(accessibilityEvent.getSource());
                break;
            }
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED: {
                for (AccessibilityWindowInfo w : getWindows()) {
                    Log.d("SJC", "TYPE_WINDOWS_CHANGED " + w.getTitle());
                }

                break;
            }
            case AccessibilityEvent.TYPE_VIEW_FOCUSED: {
                Log.d("SJC", "TYPE_VIEW_FOCUSED " + accessibilityEvent.getPackageName() + " ClassName:" + accessibilityEvent.getClassName());
                for (CharSequence charSequence : accessibilityEvent.getText()) {
                    Log.d("SJC", "TYPE_VIEW_FOCUSED:" + charSequence.toString());
                }
                break;
            }

            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER: {
                Log.d("SJC", "TYPE_VIEW_HOVER_ENTER " + accessibilityEvent.getSource().getContentDescription().toString());
                break;
            }

        }*/
    }

    /*@Override
    protected boolean onGesture(int gestureId) {
        boolean superValue = super.onGesture(gestureId);
        Log.e("SJC", "onGesture");

        return superValue;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void dumpWindow(AccessibilityNodeInfo source) {
        AccessibilityNodeInfo root = source;
        while (true) {
            AccessibilityNodeInfo parent = root.getParent();
            if (parent == null) {
                break;
            } else if (parent.equals(root)) {
                Log.i("SJC", "Node is own parent:" + root);
            }
            root = parent;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dumpTree(root, new ArraySet<AccessibilityNodeInfo>());
        }
    }

    private void dumpTree(AccessibilityNodeInfo root, Set<AccessibilityNodeInfo> visited) {
        if (root == null) {
            return;
        }

        if (!visited.add(root)) {
            Log.i("SJC", "Cycle detected to node:" + root);
        }

        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null) {
                AccessibilityNodeInfo parent = child.getParent();
                if (parent == null) {
                    Log.e("SJC", "Child of a node has no parent");
                } else if (!parent.equals(root)) {
                    Log.e("SJC", "Child of a node has wrong parent");
                }
                Log.i("SJC", child.toString());
            }
        }

        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            dumpTree(child, visited);
        }
    }*/

    @Override
    public void onInterrupt() {
        Log.d("SJC", "onInterrupt");
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i("SJC", "onServiceConnected");
    }
}
