package com.dev.zerat.goplabresult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by HP on 2.07.2017.
 */

public class ResultListAdapter extends ArrayAdapter<String> {
    String[] items_list;
    Context context;
    int vg;
    public  ResultListAdapter(Context context,int vg,int id,String[] items_list)
    {
        super(context,vg,id,items_list);
        this.context=context;
        this.items_list=items_list;
        this.vg=vg;
    }

    static  class ViewHolder{
        public TextView txtDate;
        public  TextView txtDepartment;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View rowview=convertView;
        if (rowview==null)
        {
            LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview=layoutInflater.inflate(vg,parent,false);
            ViewHolder holder=new ViewHolder();
            holder.txtDate=(TextView)rowview.findViewById(R.id.ROWLAYOUT_TEXTVIEW_DATE);
            holder.txtDepartment=(TextView)rowview.findViewById(R.id.ROWLAYOUT_TEXTVIEW_DEPARTMENT);
            rowview.setTag(holder);
        }
        String [] items=items_list[position].split("__");
        ViewHolder holder=(ViewHolder)rowview.getTag();
        holder.txtDate.setText(items[0].trim());
        holder.txtDepartment.setText(items[1].trim());
        return  rowview;

    }
}
