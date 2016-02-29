package io.github.nafanya.vkdocs.presentation.ui.views.docs;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.OneFragment;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.ThreeFragment;
import io.github.nafanya.vkdocs.TwoFragment;
import io.github.nafanya.vkdocs.domain.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.presentation.presenter.base.CommonDocumentsPresenter;

public class TabbedDocsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private EventBus eventBus;
    protected DocumentRepository repository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        eventBus = ((App) activity.getApplication()).getEventBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


        View rootView = inflater.inflate(R.layout.fragment_tabbed_docs, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(DefaultDocumentsListFragment.newInstance(new CommonDocumentsPresenter(x->true, eventBus, repository)), "ALL");
        adapter.addFragment(DefaultDocumentsListFragment.newInstance(new CommonDocumentsPresenter(x->x.ext.equals("jpg"), eventBus, repository)), "IMGS");
        adapter.addFragment(DefaultDocumentsListFragment.newInstance(new CommonDocumentsPresenter(x->x.ext.equals("mp3"), eventBus, repository)), "AUDIO");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
