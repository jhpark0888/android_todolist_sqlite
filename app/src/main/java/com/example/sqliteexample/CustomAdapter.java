package com.example.sqliteexample;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<TodoItem> mTodoItems;
    private Context mContext;
    private DBHelper mDBHelper;

    public CustomAdapter(ArrayList<TodoItem> mTodoItems, Context mContext) {
        this.mTodoItems = mTodoItems;
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext);
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

        holder.tv_title.setText(mTodoItems.get(position).getTitle());
        holder.tv_content.setText(mTodoItems.get(position).getContent());
        holder.tv_writeDate.setText(mTodoItems.get(position).getWriteDate());
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_writeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_writeDate = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curPos = getAdapterPosition(); // ?????? ????????? ????????? ????????? ??????
                    TodoItem todoItem = mTodoItems.get(curPos);

                    String[] strChoiceItems = { "????????????", "????????????"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("????????? ????????? ?????? ????????????");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if(position == 0) {
                                //????????????
                                //?????? ??? ?????????
                                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.dialog_edit);
                                EditText et_title = dialog.findViewById(R.id.et_title);
                                EditText et_content = dialog.findViewById(R.id.et_content);
                                Button btn_ok = dialog.findViewById(R.id.btn_ok);

                                et_title.setText(todoItem.getTitle());
                                et_content.setText(todoItem.getContent());

                                // ?????? ????????? ??????????????? ??????
                                et_title.setSelection(et_title.getText().length());

                                btn_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        // Update table
                                        String title = et_title.getText().toString();
                                        String content = et_content.getText().toString();
                                        String currentTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()); // ?????? ?????? ?????????????????? ????????????
                                        String beforeTime = todoItem.getWriteDate();

                                        mDBHelper.UpdateTodo(title, content, currentTime, beforeTime);

                                        // Update UI
                                        todoItem.setTitle(title);
                                        todoItem.setContent(content);
                                        todoItem.setWriteDate(currentTime);
                                        notifyItemChanged(curPos, todoItem);
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "?????? ????????? ?????? ???????????????", Toast.LENGTH_SHORT).show();

                                    }
                                });

                                dialog.show();
                            } else if(position == 1) {
                                //Delete table
                                String beforeTime = todoItem.getWriteDate();
                                mDBHelper.DeleteTodo(beforeTime);

                                //Delete UI
                                mTodoItems.remove(curPos);
                                notifyItemRemoved(curPos);
                                Toast.makeText(mContext, "????????? ?????? ???????????????", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    //?????????????????? ???????????? ????????????, ?????? ???????????? ????????? ????????? ???????????? ???????????? ???????????? ????????????.
    public void addItem(TodoItem _item) {
        mTodoItems.add(0, _item);
        notifyItemInserted(0);
    }
}
