package ru.besttuts.stockwidget.service;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class UpdateJobService extends JobService {

    private static final String TAG = makeLogTag(UpdateJobService.class);

    private static Intent mIntent;

    // schedule the start of the service every 10 - 30 seconds
    public static void scheduleJob(Context context, Intent intent) {
        mIntent = intent;

        ComponentName serviceComponent = new ComponentName(context, UpdateJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if (mIntent != null) getApplicationContext().startService(mIntent);
        LOGD(TAG, "onStartJob");
//      scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LOGD(TAG, "onStopJob");
        return true;
    }

}
