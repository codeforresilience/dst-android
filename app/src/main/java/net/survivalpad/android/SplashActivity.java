package net.survivalpad.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class SplashActivity extends FragmentActivity implements ProgressMonitor {
    private static final String TAG = "SplashActivity";

    private static final String REPOSITORY_DIR = "repo";

    private static final String REPOSITORY_URL = "https://github.com/DisasterSurvivalToolbox/dst-data";
    private static File PATH;

    private ImageView mLogo;
    private ProgressBar mProgressBar;
    private TextView mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mLogo = (ImageView) findViewById(R.id.iv_logo);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);
        mState = (TextView) findViewById(R.id.tv_state);

        PATH = getDir(REPOSITORY_DIR, MODE_PRIVATE);

        Thread th = new Thread() {
            @Override
            public void run() {
//                if (BuildConfig.DEBUG) {
//                    delete(PATH);
//                }

                boolean createFlag = (PATH.list().length == 0);

                try {
                    if (createFlag) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mState.setText("GitHubからデータをcloneしています...");
                            }
                        });

                        Repository repository = new FileRepositoryBuilder()
                                .setGitDir(PATH)
                                .readEnvironment()
                                .findGitDir()
                                .build();

                        new Git(repository).cloneRepository()
                                .setBare(false)
                                .setCloneAllBranches(false)
                                .setDirectory(PATH)
                                .setURI(REPOSITORY_URL)
                                .setProgressMonitor(SplashActivity.this)
                                .call();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setIndeterminate(true);
                                mState.setText("更新を取得しています...");
                            }
                        });

                        Repository localRepo = new FileRepository(PATH.getAbsoluteFile() + "/.git");
                        new Git(localRepo).pull()
                                .setProgressMonitor(SplashActivity.this)
                                .call();
                    }

                } catch (IOException e) {
                    Log.e(TAG, "IOException", e);
                } catch (InvalidRemoteException e) {
                    Log.e(TAG, "InvalidRemoteException", e);
                } catch (TransportException e) {
                    Log.e(TAG, "TransportException", e);
                } catch (GitAPIException e) {
                    Log.e(TAG, "GitAPIException", e);
                }
            }
        };
        th.start();
    }

    private static void delete(File path) {
        String[] list = path.list();

        for (String fileName : list) {
            File file = new File(path, fileName);
            if (file.isDirectory()) {
                delete(file);
            }
            file.delete();
        }
        path.delete();
    }

    private static void showFileList(File path) {
        String[] list = path.list();

        for (String fileName : list) {
            File file = new File(path, fileName);
            if (file.isDirectory()) {
                showFileList(file);
            } else {
                Log.d(TAG, file.getAbsolutePath());
            }
        }
    }

    @Override
    public void start(final int totalTasks) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setMax(totalTasks);
            }
        });
    }

    @Override
    public void beginTask(String title, int totalWork) {
    }

    @Override
    public void update(final int completed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setProgress(completed);
            }
        });
    }

    @Override
    public void endTask() {
        if (!isFinishing()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
