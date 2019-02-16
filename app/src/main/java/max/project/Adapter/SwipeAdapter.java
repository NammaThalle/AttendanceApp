package max.project.Adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import max.project.Fragment.ClassFilterFragment;

/**
 * Created by max on 10/7/17.
 *
 * ADAPTER TO HANDLE FRAGMENTS
 */

public class SwipeAdapter extends FragmentPagerAdapter{

    public SwipeAdapter(FragmentManager fm){ super(fm);}

    @Override
    public Fragment getItem(int i){


        switch (i){
            case 0: Fragment fragment = new ClassFilterFragment();
                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);
                return fragment;

//            case 1: Fragment fragment1 = new ReportFilterFragment();
//                Bundle bundle1 = new Bundle();
//                fragment1.setArguments(bundle1);
//                return fragment1;
//            default: break;
        }
        return null;

    }

    @Override
    public int getCount() {
        return 1;
    }
}
