package com.kavindu.farmshare.investor;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kavindu.farmshare.R;
import com.kavindu.farmshare.model.PayoutItem;
import com.kavindu.farmshare.model.TransactionItem;

import java.util.ArrayList;


public class InvestorTransactionFragment extends Fragment {


    public InvestorTransactionFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        ImageView menuButton = view.findViewById(R.id.investorTransactionMenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof InvestorMainActivity) {
                    ((InvestorMainActivity) getActivity()).openDrawer();
                }
            }
        });

        ArrayList<TransactionItem> transactionItems = new ArrayList<>();
        transactionItems.add(new TransactionItem("Test Farm 1","Investment","-15,870.00 RS","17:56"));
        transactionItems.add(new TransactionItem("Test Farm 2","Investment","-11,870.00 RS","13:26"));
        transactionItems.add(new TransactionItem("Test Farm 3","Deposit","+20,870.00 RS","18:46"));

        ArrayList<TransactionItem> oldTransactionItems = new ArrayList<>();
        oldTransactionItems.add(new TransactionItem("Test Farm 3","Deposit","+20,870.00 RS","Jan 3, 16:30"));
        oldTransactionItems.add(new TransactionItem("Test Farm 1","Investment","-15,870.00 RS","Jan 2, 11:30"));
        oldTransactionItems.add(new TransactionItem("Test Farm 2","Investment","-11,870.00 RS","Jan 2, 13:30"));
        oldTransactionItems.add(new TransactionItem("Test Farm 3","Deposit","+20,870.00 RS","Jan 1, 17:35"));

        transactionItemInflater(R.id.todayTransactionsContainer,view,transactionItems);
        transactionItemInflater(R.id.oldTransactionsContainer,view,oldTransactionItems);

        return view;
    }

    private void transactionItemInflater(int container,View parent, ArrayList<TransactionItem> itemArrayList){

        LinearLayout itemContainer = parent.findViewById(container);

        for (TransactionItem transactionItem : itemArrayList){

            View item = getLayoutInflater().inflate(R.layout.fragment_transaction_item,null);

            TextView name = item.findViewById(R.id.textView132);
            TextView type = item.findViewById(R.id.textView133);
            TextView price = item.findViewById(R.id.textView134);
            TextView time = item.findViewById(R.id.textView135);
            ImageView image = item.findViewById(R.id.imageView28);

            name.setText(transactionItem.getName());
            type.setText(transactionItem.getType());
            price.setText(transactionItem.getPrice());
            time.setText(transactionItem.getTime());

            if (transactionItem.getType().equals("Deposit")){
                image.setImageResource(R.drawable.down_left_arrow);
                image.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.transaction_bg2));
                price.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.green));
            }


            itemContainer.addView(item);

        }

    }
}