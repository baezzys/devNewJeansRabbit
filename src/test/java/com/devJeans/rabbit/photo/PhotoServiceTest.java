package com.devJeans.rabbit.photo;

import com.amazonaws.services.s3.AmazonS3;
import com.devJeans.rabbit.BunnyTestcontainers;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.repository.AccountRepository;
import com.devJeans.rabbit.repository.PhotoRepository;
import com.devJeans.rabbit.service.AccountService;
import com.devJeans.rabbit.service.PhotoService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@BunnyTestcontainers
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class PhotoServiceTest {


    @Autowired
    PhotoService photoService;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    EntityManager em;

    @MockBean
    AmazonS3 s3client;

    @Test
    @Transactional
    void userLikePhotoTest() {
        Account user = new Account("test", "test", "test", "test");
        Account user2 = new Account("test", "test", "test1", "test");

        Photo photo = new Photo("imageurl", "thumbnailurl", "keyname", "thumbnailkeyname", "title", user);

        accountRepository.save(user);
        accountRepository.save(user2);
        photoRepository.save(photo);

        photoService.likePhoto(photo , user2);

        assertEquals(user2.getLikedPhotos().contains(photo), Boolean.TRUE);
        assertEquals(photo.getLikeCount(), 1);
    }

    @Test
    @Transactional
    void deletePhotoTest() {
        Account user = new Account("test", "test", "test", "test");
        Account user2 = new Account("test", "test", "test1", "test");

        Photo photo = new Photo("imageurl", "thumbnailurl", "keyname", "thumbnailkeyname", "title", user);

        accountRepository.save(user);
        accountRepository.save(user2);
        photoRepository.save(photo);

        photoService.likePhoto(photo , user2);
        photoService.likePhoto(photo, user);
        photoService.deletePhoto(user.getId(), photo.getId());

        assertEquals(user.getCreatedPhotos().size(), 0);

    }

    @Test
    @Transactional
    public void testLikePhotoServiceConcurrently() throws Exception {

        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Account account = new Account("user1", "password1", "John Doe" + i, "test");
            accountList.add(account);
            accountRepository.save(account);
        }

        Photo photo = new Photo("http://example.com/image.jpg", "http://example.com/thumbnail.jpg", "image.jpg", "thumbnail.jpg", "Test photo", accountList.get(0));
        photoRepository.save(photo);

        int threadNum = 50;
        // Create two threads that will execute the likePhoto() method simultaneously
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            Account account = accountList.get(i);
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    photoService.likePhoto(photo, account);
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        Photo updatedPhoto = photoRepository.findById(photo.getId()).get();
        System.out.println("테스트 결과 : " + updatedPhoto.getLikeCount());
        assertEquals(50, updatedPhoto.getLikeCount());
        assertEquals(50, updatedPhoto.getUserLiked().size());

        for (Account account : accountList) {
            assertEquals(account.getLikedPhotos().size(), 1);
        }
    }

    @Test
    public void testLikePhotoControllerConcurrently() throws InterruptedException {
        List<Account> accountList = new ArrayList<>();
        List<Principal> principals = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Account account = new Account("user1", "password1", "John Doe" + i, "test");
            accountList.add(account);
            accountRepository.save(account);
            MockPrincipal principal = new MockPrincipal(String.valueOf(account.getId()));
            principals.add(principal);
        }

        Photo photo = new Photo("http://example.com/image.jpg", "http://example.com/thumbnail.jpg", "image.jpg", "thumbnail.jpg", "Test photo", accountList.get(0));
        Photo savedPhoto = photoRepository.save(photo);

        List<Account> savedAccountList = accountRepository.findAll();

        int threadNum = 10;
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            Account account = savedAccountList.get(i);
            Principal principal = principals.get(i);
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mockMvc.perform(post("/photo/like/{id}", savedPhoto.getId())
                                        .principal(principal)
                                        .contentType(MediaType.APPLICATION_JSON));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        Photo result = photoRepository.findById(savedPhoto.getId()).get();
        assertEquals(result.getLikeCount(), 10);
    }

    class MockPrincipal implements Principal {

        private final String name;

        public MockPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean implies(Subject subject) {
            return Principal.super.implies(subject);
        }
    }

}
