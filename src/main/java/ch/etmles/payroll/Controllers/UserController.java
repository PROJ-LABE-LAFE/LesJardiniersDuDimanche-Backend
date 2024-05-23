package ch.etmles.payroll.Controllers;

import ch.etmles.payroll.Entities.User;
import ch.etmles.payroll.Repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class UserController {

    private final UserRepository repository;

    UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/users")
    List<User> all() {
        return repository.findAll();
    }

    @PostMapping("/users")
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    @GetMapping("/users/{id}")
    User one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {
        return repository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    user.setEmail(newUser.getEmail());
                    user.setConnected(newUser.isConnected());
                    user.setWallet(newUser.getWallet());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PostMapping("/users/{id}/credit")
    User creditWallet(@PathVariable Long id, @RequestBody BigDecimal amount) {
        return repository.findById(id)
                .map(user -> {
                    user.setWallet(user.getWallet().add(amount));
                    return repository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PostMapping("/users/{id}/debit")
    User debitWallet(@PathVariable Long id, @RequestBody BigDecimal amount) {
        return repository.findById(id)
                .map(user -> {
                    user.setWallet(user.getWallet().subtract(amount));
                    return repository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
