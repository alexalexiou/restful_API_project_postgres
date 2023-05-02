package com.example.rest_web_services_coding_assig.controller;

import com.example.rest_web_services_coding_assig.ProductNotFoundException;
import com.example.rest_web_services_coding_assig.model.AuthenticationRequest;
import com.example.rest_web_services_coding_assig.model.AuthenticationResponse;
import com.example.rest_web_services_coding_assig.model.Product;
import com.example.rest_web_services_coding_assig.repository.ProductRepository;
import com.example.rest_web_services_coding_assig.service.MyUserDetailsService;
import com.example.rest_web_services_coding_assig.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class ProductRestController {
    private final ProductRepository prodRepository;
    private final AuthenticationProvider authenticationProvider;
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

    public ProductRestController(ProductRepository prodRepository, AuthenticationProvider authenticationProvider,
                                 MyUserDetailsService myUserDetailsService, JwtUtil jwtUtil) {
        this.prodRepository = prodRepository;
        this.authenticationProvider = authenticationProvider;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or Password", e);
        }
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @GetMapping("/api/products")
    public List<Product> getAll(){
        return prodRepository.findAll();
    }

    @GetMapping("/api/products/name/{name}")
    public List<Product> getProductByName(@PathVariable("name") String name){
        List<Product> products = prodRepository.findByName(name);
        if(!products.isEmpty())
            return prodRepository.findByName(name);
        else
            throw new ProductNotFoundException(name);
    }

    @GetMapping("/api/products/price/asc")
    public List<Product> getProductByPriceAsc(){
        return prodRepository.findAllByOrderByPriceAsc();
    }


    @GetMapping("/api/products/price/desc")
    public List<Product> getProductByPriceDesc(){
        return prodRepository.findAllByOrderByPriceDesc();
    }

    @GetMapping("/api/products/code/{code}")
    public List<Product> getProductByCode(@PathVariable("code") String code) {
        List<Product> products = prodRepository.findByCode(code);
        if(!products.isEmpty())
            return prodRepository.findByCode(code);
        else
            throw new ProductNotFoundException(code, code);
    }

    @PostMapping("/api/products")
    public HttpStatus addProduct(@RequestBody Product product){
         prodRepository.save(product);
         return HttpStatus.OK;
    }

    @PutMapping("/api/products")
    public List<Product> saveOrUpdate(@RequestBody Product product){
        prodRepository.save(product);
        return Collections.singletonList(product);
    }

    @DeleteMapping("/api/products/{id}")
    public HttpStatus delete(@PathVariable("id") long id) {
        Product product =  prodRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException(id));
        prodRepository.delete(product);
        return HttpStatus.OK;
    }
}
