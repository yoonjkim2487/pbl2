package com.example.greenlens.view.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.model.User;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<User> userList = new ArrayList<>();

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking_user, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        User user = userList.get(position);

        // 순위 표시 (1-based index)
        int rank = position + 1;
        holder.textRank.setText(String.valueOf(rank));

        // 특별한 순위(1, 2, 3위)에 대한 메달 아이콘 표시 및 숫자 색상 설정
        if (rank == 1) {
            holder.imageRank.setImageResource(R.drawable.ic_medal_gold);
            holder.imageRank.setVisibility(View.VISIBLE);
            // 금메달 위의 숫자는 어두운 주황색으로 (밝은 금색에 잘 보이도록)
            holder.textRank.setTextColor(Color.parseColor("#B8860B"));
        } else if (rank == 2) {
            holder.imageRank.setImageResource(R.drawable.ic_medal_silver);
            holder.imageRank.setVisibility(View.VISIBLE);
            // 은메달 위의 숫자는 어두운 회색으로
            holder.textRank.setTextColor(Color.parseColor("#696969"));
        } else if (rank == 3) {
            holder.imageRank.setImageResource(R.drawable.ic_medal_bronze);
            holder.imageRank.setVisibility(View.VISIBLE);
            // 동메달 위의 숫자는 갈색으로
            holder.textRank.setTextColor(Color.parseColor("#684932"));
        } else {
            // 4위부터는 메달 없이 숫자만 표시
            holder.imageRank.setVisibility(View.INVISIBLE);
            holder.textRank.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
        }

        // 사용자 이름과 배출 횟수 표시
        holder.textUsername.setText(user.getUsername());
        holder.textRecycleCount.setText(String.format("%d회", user.getRecycleCount()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView textRank;
        ImageView imageRank;
        TextView textUsername;
        TextView textRecycleCount;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            textRank = itemView.findViewById(R.id.textRank);
            imageRank = itemView.findViewById(R.id.imageRank);
            textUsername = itemView.findViewById(R.id.textUsername);
            textRecycleCount = itemView.findViewById(R.id.textRecycleCount);
        }
    }
}