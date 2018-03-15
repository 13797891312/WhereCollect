package com.gongwu.wherecollect.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;

import com.gongwu.wherecollect.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhaojin on 2016/8/12.
 */
public abstract class DateBirthdayDialog {
    private Context context;
    private String date;
    private DatePicker datePicker;

    /**
     * @param context
     * @param date    字符串，不为时间戳
     */
    public DateBirthdayDialog(Context context, String date) {
        this.context = context;
        this.date = date;
    }

    public void show() {
        View view = View.inflate(context, R.layout.dialog_birthday_picker, null);
        datePicker = (DatePicker) view.findViewById(R.id.new_act_date_picker);
        int year;
        int month;
        int day;
        if (!TextUtils.isEmpty(date)) {
            try {
                String[] d = date.split("-");
                year = Integer.valueOf(d[0]);
                month = Integer.valueOf(d[1]) - 1;
                day = Integer.valueOf(d[2]);
                datePicker.updateDate(year, month, day);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                datePicker.init(year, month, day, null);
            }
        } else {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            datePicker.init(year, month, day, null);
        }
        // Build DateTimeDialog
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context,
                AlertDialog.THEME_HOLO_LIGHT);
        builder.setView(view);
        builder.setTitle("选择时间");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //                arrive_month = datePicker.getMonth();
                //                arrive_day = datePicker.getDayOfMonth();
                //                String dateStr = DateUtil.formatDate(arrive_year, arrive_month, arrive_day);
                //                arriveDateBtn.setText(dateStr);
                //                arrive_hour = timePicker.getCurrentHour();
                //                arrive_min = timePicker.getCurrentMinute();
                //                String timeStr = DateUtil.formatTime(arrive_hour, arrive_min);
                result(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
            }
        });
        builder.show();
    }

    public abstract void result(int year, int month, int day);

    public void setDateMax() {
        if (Build.VERSION.SDK_INT >= 11) {
            datePicker.setMaxDate(new Date().getTime());
        }
    }
}


