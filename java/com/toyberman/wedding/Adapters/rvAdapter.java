package com.toyberman.wedding.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.toyberman.wedding.Entities.Contact;
import com.toyberman.wedding.LoginActivity;
import com.toyberman.wedding.R;

import java.util.List;

/**
 * Created by Maxim Toyberman on 1/10/15.
 */
public class rvAdapter extends RecyclerView.Adapter<rvAdapter.CustomViewHolder> {
    private List<Contact> contacts;
    private Context ctx;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextViewName;
        public CheckBox mCheckBox;
        public TextView mTextViewNumber;

        public CustomViewHolder(View view) {
            super(view);
            this.mTextViewName = (TextView) view.findViewById(R.id.tv_name);
            this.mCheckBox = (CheckBox) view.findViewById(R.id.cb_check_box);
            this.mTextViewNumber = (TextView) view.findViewById(R.id.tv_number);


            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    Contact contact= (Contact) buttonView.getTag();
                    if(isChecked){
                        contact.setSelected(true);
                    }
                    else{
                        contact.setSelected(false);
                    }
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public rvAdapter(List<Contact> contacts,Context ctx) {

        this.contacts = contacts;
        this.ctx=ctx;

        for (Contact contact  : contacts) {
            contact.setSelected(false);

        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(ctx)
                .inflate(R.layout.contacts_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        CustomViewHolder vh = new CustomViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomViewHolder holder,int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = contacts.get(position).getName();
        String number = contacts.get(position).getPhone_number();
        Contact contact=contacts.get(position);
        Boolean selected = contact.isSelected();
        holder.mTextViewName.setText(name);
        holder.mTextViewNumber.setText(number);

        holder.mCheckBox.setTag(contact);

        if(selected){
            holder.mCheckBox.setChecked(true);
        }
        else{
            holder.mCheckBox.setChecked(false);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return contacts.size();
    }


    public Contact removeItem(int position) {
        final Contact contact = contacts.remove(position);
        notifyItemRemoved(position);
        return contact;
    }

    public void addItem(int position, Contact contact) {
        contacts.add(position, contact);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Contact contact = contacts.remove(fromPosition);
        contacts.add(toPosition, contact);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<Contact> contacts) {
        applyAndAnimateRemovals(contacts);
        applyAndAnimateAdditions(contacts);
        applyAndAnimateMovedItems(contacts);
    }

    private void applyAndAnimateRemovals(List<Contact> newModels) {
        for (int i = contacts.size() - 1; i >= 0; i--) {
            final Contact contact = contacts.get(i);
            if (!newModels.contains(contact)) {
                removeItem(i);
            }
        }
    }
    private void applyAndAnimateAdditions(List<Contact> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Contact model = newModels.get(i);
            if (!contacts.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Contact> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Contact model = newModels.get(toPosition);
            final int fromPosition = contacts.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

}