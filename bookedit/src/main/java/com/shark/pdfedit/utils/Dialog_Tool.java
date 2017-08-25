//package com.shark.pdfedit.utils;
//
//import android.app.Activity;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.shark.pdfedit.R;
//import com.shark.pdfedit.adapter.BookDetailBuilder;
//import com.shark.pdfedit.statich.BookStatic;
//import com.shark.pdfedit.widget.AutoDialogBuilder;
//import com.wisdomregulation.data.entitybase.Base_Entity;
//import com.wisdomregulation.data.entitydemo.Entity_Demo;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class Dialog_Tool {
//
//
//    /**
//     * 弹出的文书中增加tip的弹窗
//     * @param activity
//     * @param entity
//     * @param back
//     * @return
//     */
//    public static View showDialog_AddBookItem(final Activity activity, Object entity, final CallBack back) {
//        LinearLayout dialogview = (LinearLayout) activity
//                .getLayoutInflater().inflate(R.layout.dialog_add_item, null);
////        Util_MatchTip.initAllScreenText(dialogview);
//        TextView dialogtitle = (TextView) dialogview.findViewById(R.id.dialogtitle);
//        LinearLayout itemcontent = (LinearLayout) dialogview.findViewById(R.id.itemcontent);
//
//        String org = (String) entity;
//        String result = "";
//        String[] orgarray = org.split("3");
//        String[] orgarray2 = orgarray[1].split("2");
//        result = orgarray[0].trim().replace("list", "");
//        Pattern pattern=Pattern.compile("(.*?)lim(.*)");
//        Matcher matcher=pattern.matcher(result);
//        if(matcher.find()){
//            result=matcher.group(1)+"-->限制:"+matcher.group(2);
//        }
//        dialogtitle.setText(result);
//        Base_Entity itementity = new Entity_Demo();
//        for (int i = 0; i < orgarray2.length; i++) {
//            itementity.add(orgarray2[i].trim(), "");
//        }
//        final BookDetailBuilder adapter = new BookDetailBuilder(activity, itementity, itemcontent);
//        adapter.setEditState(true).build();
//        AutoDialogBuilder builder = new AutoDialogBuilder(activity, dialogview,
//                new LayoutParams((int) (BookStatic.create().getAppScreenWidth() / 1.1), (int) (BookStatic.create().getAppScreenHigh() / 1.5)));
//        builder.setPositiveButton(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                Base_Entity resultentity = adapter.getResult();
//                back.back(resultentity);
//
//            }
//        }).setNegativeButton(null);
//        builder.show();
//        return dialogview;
//    }
//
//
//}
