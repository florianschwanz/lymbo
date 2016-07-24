package de.interoberlin.lymbo.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.core.model.v1.impl.Language;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.util.Base64BitmapConverter;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.components.TagView;

public class StacksListAdapter extends ArrayAdapter<Stack> {
    // <editor-fold defaultstate="expanded" desc="Members">

    // Context
    private Context context;
    private OnCompleteListener ocListener;

    // View
    static class ViewHolder {
        LinearLayout v;
        @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvSubtitle) TextView tvSubtitle;
        @BindView(R.id.ivShare) ImageView ivShare;
        @BindView(R.id.ivUpload) ImageView ivUpload;
        @BindView(R.id.tvCardCount) TextView tvCardCount;
        @BindView(R.id.llTags) LinearLayout llTags;
        @BindView(R.id.tvPath) TextView tvPath;
        @BindView(R.id.tvError) TextView tvError;

        public ViewHolder(View v) {
            this.v = (LinearLayout) v;
            ButterKnife.bind(this, v);
        }
    }

    // Controller
    private StacksController stacksController;
    private CardsController cardsController;

    // Filter
    private List<Stack> filteredItems = new ArrayList<>();
    private List<Stack> originalItems = new ArrayList<>();
    private LymboListFilter lymboListFilter;
    private final Object lock = new Object();

    // </editor-fold>

    // --------------------
    // Constructors
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Constructors">

    public StacksListAdapter(Context context, OnCompleteListener ocListener, int resource, List<Stack> items) {
        super(context, resource, items);
        stacksController = StacksController.getInstance();
        cardsController = CardsController.getInstance();

        this.filteredItems = items;
        this.originalItems = items;

        this.context = context;
        this.ocListener = ocListener;

        filter();
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Methods">

    @Override
    public int getCount() {
        return filteredItems != null ? filteredItems.size() : 0;
    }

    @Override
    public Stack getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final Stack stack = getItem(position);

        final ViewHolder viewHolder;

        if (stack.getError() == null) {
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
                viewHolder = new ViewHolder(v);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            // final ViewHolder viewHolderFinal = viewHolder;

            // Tint
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.ivShare.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
                viewHolder.ivUpload.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
            }

            // Set values
            if (stack.getImageFormat() != null && stack.getImage() != null && !stack.getImage().trim().isEmpty()) {
                switch (stack.getImageFormat()) {
                    case BASE_64: {
                        Bitmap bmp = Base64BitmapConverter.decodeBase64(stack.getImage());
                        viewHolder.ivImage.setImageBitmap(bmp);
                        break;
                    }
                    case REF: {
                        String imagePath = stack.getPath() + "/" + stack.getImage();
                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                        viewHolder.ivImage.setImageBitmap(bmp);
                        viewHolder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        break;
                    }
                }
            } else {
                viewHolder.ivImage.getLayoutParams().height = 0;
            }

            if (stack.getTitle() != null)
                viewHolder.tvTitle.setText(stack.getTitle());
            if (stack.getSubtitle() != null)
                viewHolder.tvSubtitle.setText(stack.getSubtitle());

            // Context menu
            viewHolder.v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    if (!stack.isAsset()) {
                        contextMenu.add(0, 0, 0, getResources().getString(R.string.edit))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        ocListener.onClickEdit(stack);
                                        return false;
                                    }
                                });
                        contextMenu.add(0, 1, 0, getResources().getString(R.string.stash_stack))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        Animation a = ViewUtil.collapse(context, viewHolder.v);
                                        viewHolder.v.startAnimation(a);

                                        a.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                ocListener.onClickStash(position, stack);
                                                filter();
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });

                                        return false;
                                    }
                                });
                    }
                }
            });

            // Add tags
            viewHolder.llTags.removeAllViews();
            for (Tag tag : stack.getTags()) {
                if (!tag.getValue().equals(getResources().getString(R.string.no_tag))) {
                    TagView cvTag = new TagView(context, tag);
                    cvTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ocListener.onClickSelectTags();
                        }
                    });

                    viewHolder.llTags.addView(cvTag);
                }
            }

            // Add languages
            Language language = stack.getLanguage();
            if (language != null && language.getFrom() != null && language.getTo() != null) {
                Tag languageFromTag = new Tag(language.getFrom());
                TagView cvTagLanguageFrom = new TagView(context, languageFromTag);
                cvTagLanguageFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ocListener.onClickSelectTags();
                    }
                });

                viewHolder.llTags.addView(cvTagLanguageFrom);

                Tag languageToTag = new Tag(language.getTo());
                TagView cvTagLanguageTo = new TagView(context, languageToTag);
                cvTagLanguageTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ocListener.onClickSelectTags();
                    }
                });

                viewHolder.llTags.addView(cvTagLanguageTo);
            }

            // Action : open cards view
            viewHolder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardsController.setStack(stack);
                    cardsController.init(getContext());
                    Intent openStartingPoint = new Intent(context, CardsActivity.class);
                    context.startActivity(openStartingPoint);
                }
            });

            // Action : share
            if (!stack.isAsset()) {
                viewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ocListener.onClickSend(stack);
                    }
                });
            } else {
                ViewUtil.remove(viewHolder.ivShare);
            }

            // Action : upload
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String author = stack.getAuthor();
            String noAuthorSpecified = getResources().getString(R.string.no_author_specified);
            String username = prefs.getString(getResources().getString(R.string.pref_lymbo_web_user_name), null);

            if (author == null || author.isEmpty() || author.equals(noAuthorSpecified) || author.equals(username)) {
                viewHolder.ivUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ocListener.onClickUpload(stack);
                    }
                });
            } else {
                ViewUtil.remove(viewHolder.ivUpload);
            }

            // Card count
            viewHolder.tvCardCount.setText(String.valueOf(stack.getCards().size() + " " + context.getResources().getString(R.string.cards)));

            return v;
        } else {
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
                viewHolder = new ViewHolder(v);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            // Set values
            viewHolder.tvTitle.setText(getResources().getString(R.string.broken_lymbo_file));

            // Context menu
            viewHolder.v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    if (!stack.isAsset()) {
                        contextMenu.add(0, 0, 0, getResources().getString(R.string.stash_stack))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        stash(position, stack, viewHolder.v);
                                        return false;
                                    }
                                });
                    }
                }
            });

            if (stack.getPath() != null)
                viewHolder.tvPath.setText(stack.getFile());

            if (stack.getError() != null)
                viewHolder.tvError.setText(stack.getError());

            return v;
        }
    }

    // </editor-fold>

    // --------------------
    // Methods - Actions
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Actions">

    /**
     * Performs stash animation
     *
     * @param position position of item
     * @param stack    stack to be stashed
     * @param llStack  corresponding view
     */
    private void stash(final int position, final Stack stack, final LinearLayout llStack) {
        Animation a = ViewUtil.collapse(context, llStack);
        llStack.startAnimation(a);

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ocListener.onClickStash(position, stack);
                filter();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    // </editor-fold>

    // --------------------
    // Methods - Filter
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Filter">

    /*
    public List<Stack> getFilteredItems() {
        return filteredItems;
    }
    */

    public void filter() {
        getFilter().filter("");
    }

    @Override
    public Filter getFilter() {
        if (lymboListFilter == null) {
            lymboListFilter = new LymboListFilter();
        }
        return lymboListFilter;
    }

    /**
     * Determines if a stack shall be displayed
     *
     * @param stack stack
     * @return true if item is visible
     */
    protected boolean filterLymbo(Stack stack) {
        return stacksController.isVisible(stack);
    }

    // </editor-fold>

    // --------------------
    // Methods - Util
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Util">

    private Resources getResources() {
        return getContext().getResources();
    }

    // </editor-fold>

    // --------------------
    // Inner classes
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Inner classes">

    public class LymboListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            // Copy items
            originalItems = stacksController.getStacks();

            ArrayList<Stack> values;
            synchronized (lock) {
                values = new ArrayList<>(originalItems);
            }

            final int count = values.size();
            final ArrayList<Stack> newValues = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                final Stack value = values.get(i);
                if (filterLymbo(value)) {
                    newValues.add(value);
                }
            }

            results.values = newValues;
            results.count = newValues.size();

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (List<Stack>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    // </editor-fold>

    // --------------------
    // Callback interfaces
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Callback interfaces">

    public interface OnCompleteListener {
        void onClickEdit(Stack stack);

        void onClickStash(int position, Stack stack);

        void onClickSelectTags();

        void onClickSend(Stack stack);

        void onClickUpload(Stack stack);
    }

    // </editor-fold>
}