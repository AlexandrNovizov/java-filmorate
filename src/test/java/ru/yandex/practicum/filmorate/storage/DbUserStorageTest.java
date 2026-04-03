package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.storage")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbUserStorageTest {

    private final DbUserStorage userStorage;

    @Test
    void testCreateUserShouldAddUser() {
        User createdUser = User.builder()
                .name("test")
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        Collection<User> allUsers = userStorage.getAll();

        assertThat(allUsers)
                .matches(Collection::isEmpty);

        userStorage.create(createdUser);

        allUsers = userStorage.getAll();

        assertThat(allUsers)
                .matches(u -> u.size() == 1);
    }

    @Test
    void testFindUserByIdShouldReturnUser() {
        User createdUser = User.builder()
                .name("test")
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        userStorage.create(createdUser);

        long expectedId = 1L;

        Optional<User> userOptional = userStorage.getById(expectedId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("id", expectedId)
                        .hasFieldOrPropertyWithValue("name", "test")
                );
    }

    @Test
    void testGetAllUsersShouldReturnAllUsers() {
        User user1 = User.builder()
                .name("test")
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        User user2 = User.builder()
                .name("test2")
                .login("tesla2")
                .email("test2@mail.ru")
                .birthday(LocalDate.now())
                .build();

        userStorage.create(user1);
        userStorage.create(user2);
        Collection<User> users = userStorage.getAll();

        assertThat(users)
                .matches(allUsers -> allUsers.size() == 2)
                .matches(allUsers -> allUsers.contains(user1))
                .matches(allUsers -> allUsers.contains(user2));
    }

    @Test
    void testUpdateUserShouldChangeUser() {
        String initialName = "test";
        String updatedName = "updated test";
        User createdUser = User.builder()
                .name(initialName)
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        userStorage.create(createdUser);

        createdUser.setName(updatedName);
        User updatedUser = userStorage.update(createdUser);

        assertThat(updatedUser)
                .matches(user -> user.getName().equals(updatedName));
    }

    @Test
    void testAddFriendShouldAddFriend() {
        User user = User.builder()
                .name("test")
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        User friend = User.builder()
                .name("test2")
                .login("tesla2")
                .email("test2@mail.ru")
                .birthday(LocalDate.now())
                .build();

        userStorage.create(user);
        userStorage.create(friend);

        userStorage.addToFriends(user, friend);

        Collection<User> friends = userStorage.getFriends(user);

        assertThat(friends)
                .matches(receivedFriends -> receivedFriends.size() == 1);

        assertThat(friends.toArray()[0])
                .matches(receivedFriend -> receivedFriend.equals(friend));

    }

    @Test
    void testAddFriendShouldNotAddFriendToSecondUser() {
        User user = User.builder()
                .name("test")
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        User friend = User.builder()
                .name("test2")
                .login("tesla2")
                .email("test2@mail.ru")
                .birthday(LocalDate.now())
                .build();

        userStorage.create(user);
        userStorage.create(friend);

        userStorage.addToFriends(user, friend);

        Collection<User> friends = userStorage.getFriends(friend);

        assertThat(friends)
                .matches(Collection::isEmpty);

    }

    @Test
    void testRemoveFriendShouldRemoveFriend() {
        User user = User.builder()
                .name("test")
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        User friend = User.builder()
                .name("test2")
                .login("tesla2")
                .email("test2@mail.ru")
                .birthday(LocalDate.now())
                .build();

        userStorage.create(user);
        userStorage.create(friend);

        userStorage.addToFriends(user, friend);
        userStorage.removeFromFriends(user, friend);

        Collection<User> friends = userStorage.getFriends(user);

        assertThat(friends)
                .matches(Collection::isEmpty);
    }

    @Test
    void testRemoveFriendShouldNotRemoveFriendFromBothUsers() {
        User user = User.builder()
                .name("test")
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        User friend = User.builder()
                .name("test2")
                .login("tesla2")
                .email("test2@mail.ru")
                .birthday(LocalDate.now())
                .build();

        userStorage.create(user);
        userStorage.create(friend);

        userStorage.addToFriends(user, friend);
        userStorage.addToFriends(friend, user);
        userStorage.removeFromFriends(user, friend);

        Collection<User> friends = userStorage.getFriends(friend);

        assertThat(friends)
                .matches(receivedFriends -> receivedFriends.size() == 1);
    }

    @Test
    void testGetCommonFriendsShouldReturnCommonFriends() {
        User user1 = User.builder()
                .name("test")
                .login("tesla")
                .email("test@mail.ru")
                .birthday(LocalDate.now())
                .build();

        User user2 = User.builder()
                .name("test2")
                .login("tesla2")
                .email("test2@mail.ru")
                .birthday(LocalDate.now())
                .build();

        User friend = User.builder()
                .name("friend")
                .login("franch_bulka")
                .email("friend@mail.ru")
                .birthday(LocalDate.now())
                .build();

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(friend);

        userStorage.addToFriends(user1, friend);
        userStorage.addToFriends(user2, friend);

        Collection<User> commonFriends = userStorage.getCommonFriends(user1, user2);

        assertThat(commonFriends)
                .matches(collection -> collection.size() == 1);

        assertThat(commonFriends.toArray()[0])
                .hasFieldOrPropertyWithValue("name", "friend");
    }

}