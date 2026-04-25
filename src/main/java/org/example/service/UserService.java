package org.example.service;

import org.example.exception.DuplicateUserException;
import org.example.exception.InvalidEmailException;
import org.example.exception.UserNotFoundException;
import org.example.model.Librarian;
import org.example.model.Member;
import org.example.model.User;

import java.util.*;
import java.util.regex.Pattern;

public class UserService {
    private Map<String, User> userDatabase; // Lưu user theo ID (key = id, value = User object)

    public UserService() {
        this.userDatabase = new HashMap<>();
    }

    //Tạo Member mới
    public Member createMember(String id, String name,String email)
        throws DuplicateUserException, InvalidEmailException {

        //Kiểm tra ID duy nhất
        if (userDatabase.containsKey(id)) {
            throw new DuplicateUserException("Id người dùng " + id + "đã tồn tại.");
        }
        //Validate email đúng định dạng
        validateEmail(email);

        //Tạo Member
        Member member = new Member(id, name,email);
        userDatabase.put(id,member);
        return member;
        }

        //Tạo Librarian (thủ thư) chỉ dành cho admin, không public cho người dùng thường
        public Librarian createLibrarian(String id,String name,String email)
            throws DuplicateUserException, InvalidEmailException {

        if (userDatabase.containsKey(id)) {
            throw new DuplicateUserException("Id người dùng " + id + " đã tồn tại.");
        }
        validateEmail(email);
        Librarian librarian = new Librarian(id, name, email);
        userDatabase.put(id, librarian);
        return librarian;
        }

        //Kiểm tra email đúng định dạng: example@domain.com
        private void validateEmail(String email)
            throws InvalidEmailException {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);

            if(email == null || email.trim().isEmpty() || !pattern.matcher(email).matches()) {
                throw new InvalidEmailException("Email không hợp lệ: " + email + ". Email phải có dạng example@domain.com");

            }
        }

        //Tìm user theo ID
        public User findUserById(String id)
            throws UserNotFoundException {
        User user = userDatabase.get(id);
        if (user == null) {
            throw new UserNotFoundException("Không tìm thấy người dùng có Id: " + id);
        }
        return user;
        }

        //Kiểm tra user có tồn tại không?
        public boolean isUserExist(String id) {
        return userDatabase.containsKey(id);
        }

        //Lấy tất cả user trong hệ thống
        public Collection<User> getAllUsers() {
        return userDatabase.values();
    }

    //Lấy tất cả Member
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        for (User user : userDatabase.values()) {
            if (user instanceof Member) {
                members.add((Member) user);
            }
        }
        return members;
    }

    //Xóa user khỏi hệ thống
    public void deleteUser(String id)
        throws UserNotFoundException {
        if (!userDatabase.containsKey(id)) {
            throw new UserNotFoundException("Không tìm thấy người dùng Id: " + id);
        }
        userDatabase.remove(id);
    }
}

