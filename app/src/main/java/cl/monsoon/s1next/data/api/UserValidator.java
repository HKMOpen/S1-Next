package cl.monsoon.s1next.data.api;

import android.text.TextUtils;

import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.model.Account;
import cl.monsoon.s1next.data.api.model.wrapper.FavouritesWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.ForumGroupsWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.PostsWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.ResultWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.ThreadsWrapper;

public final class UserValidator {

    private static final String INVALID_UID = "0";

    private final User mUser;

    public UserValidator(User user) {
        this.mUser = user;
    }

    /**
     * Intercepts the data in order to check whether current user's login status
     * has changed and update user's status if needed.
     *
     * @param d   The data we want to intercept.
     * @param <D> The data type.
     * @return Original data.
     */
    public <D> D validateIntercept(D d) {
        Account account = null;
        if (d instanceof PostsWrapper) {
            account = ((PostsWrapper) d).getPosts();
        } else if (d instanceof ThreadsWrapper) {
            account = ((ThreadsWrapper) d).getThreads();
        } else if (d instanceof ForumGroupsWrapper) {
            account = ((ForumGroupsWrapper) d).getForumGroups();
        } else if (d instanceof FavouritesWrapper) {
            account = ((FavouritesWrapper) d).getFavourites();
        } else if (d instanceof ResultWrapper) {
            account = ((ResultWrapper) d).getAccount();
        }

        if (account != null) {
            validate(account);
        }

        return d;
    }

    /**
     * Checks current user's login status and updates {@link User}'s in our app.
     */
    public void validate(Account account) {
        final boolean logged = mUser.isLogged();
        String uid = account.getUid();
        if (INVALID_UID.equals(uid) || TextUtils.isEmpty(uid)) {
            if (logged) {
                // if account has expired
                mUser.setUid(null);
                mUser.setName(null);
                mUser.setLogged(false);
            }
        } else {
            if (!logged) {
                // if account has logged
                mUser.setUid(uid);
                mUser.setName(account.getUsername());
                mUser.setLogged(true);
            }
        }
        mUser.setPermission(account.getPermission());
        mUser.setAuthenticityToken(account.getAuthenticityToken());
    }
}
