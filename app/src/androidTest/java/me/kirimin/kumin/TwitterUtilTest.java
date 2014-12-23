package me.kirimin.kumin;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import me.kirimin.kumin.model.User;
import me.kirimin.kumin.twitter.TwitterUtil;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
public class TwitterUtilTest {

    @Test
    public void searchNextAccountは一致したAccountの次の要素を返す() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("Id1", "kirimin", "token", "secret"));
        userList.add(new User("Id2", "sashimin", "token", "secret"));
        userList.add(new User("Id3", "nemumin", "token", "secret"));
        User result = TwitterUtil.searchNextAccount(userList, "kirimin");

        assertThat(result.getSName(), is("sashimin"));
        assertThat(result.getId(), is("Id2"));
    }

    @Test
    public void searchNextAccountは最後の要素と一致したら最初の要素を返す() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("Id1", "kirimin", "token", "secret"));
        userList.add(new User("Id2", "sashimin", "token", "secret"));
        userList.add(new User("Id3", "nemumin", "token", "secret"));
        User result = TwitterUtil.searchNextAccount(userList, "nemumin");

        assertThat(result.getSName(), is("kirimin"));
        assertThat(result.getId(), is("Id1"));
    }

    @Test
    public void searchNextAccountは一致しなければnullを返す() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("Id1", "kirimin", "token", "secret"));
        userList.add(new User("Id2", "sashimin", "token", "secret"));
        userList.add(new User("Id3", "nemumin", "token", "secret"));
        User result = TwitterUtil.searchNextAccount(userList, "kirimim");

        assertNull(result);
    }

    @Test
    public void searchNextAccountはリストの要素が一つしかなければ一致した要素を返す() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("Id1", "kirimin", "token", "secret"));
        User result = TwitterUtil.searchNextAccount(userList, "kirimin");

        assertThat(result.getSName(), is("kirimin"));
        assertThat(result.getId(), is("Id1"));
    }

    @Test
    public void searchNextHashTagは一致したTagの次の要素を返す() {
        List<String> tagList = new ArrayList<>();
        tagList.add("tagA");
        tagList.add("tagB");
        tagList.add("tagC");
        String result = TwitterUtil.searchNextHashTag(tagList, "tagA");

        assertThat(result, is("tagB"));
    }

    @Test
    public void searchNextHashTagは最後の要素と一致したら最初の要素を返す() {
        List<String> tagList = new ArrayList<>();
        tagList.add("tagA");
        tagList.add("tagB");
        tagList.add("tagC");
        String result = TwitterUtil.searchNextHashTag(tagList, "tagC");

        assertThat(result, is("tagA"));
    }

    @Test
    public void searchNextHashTagは一致しなければnullを返す() {
        List<String> tagList = new ArrayList<>();
        tagList.add("tagA");
        tagList.add("tagB");
        tagList.add("tagC");
        String result = TwitterUtil.searchNextHashTag(tagList, "tagD");

        assertNull(result);
    }

    @Test
    public void searchNextHashTagはリストの要素が一つしかなければ一致した要素を返す() {
        List<String> tagList = new ArrayList<>();
        tagList.add("tagA");
        String result = TwitterUtil.searchNextHashTag(tagList, "tagA");

        assertThat(result, is("tagA"));
    }
}