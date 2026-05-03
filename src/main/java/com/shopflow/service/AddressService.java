package com.shopflow.service;

import com.shopflow.dto.request.AddressCreateRequest;
import com.shopflow.dto.response.AddressResponse;
import com.shopflow.entity.Address;
import com.shopflow.entity.User;
import com.shopflow.repository.AddressRepository;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressResponse createAddress(Long userId, AddressCreateRequest request) {
        log.info("Creating new address for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Address address = Address.builder()
                .user(user)
                .street(request.getStreet())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .build();

        // Si c'est l'adresse principale, retirer le flag des autres
        if (address.getIsPrimary()) {
            user.getAddresses().forEach(a -> a.setIsPrimary(false));
        }

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long id, Long userId, com.shopflow.entity.UserRole role) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        
        if (role != com.shopflow.entity.UserRole.ADMIN && !address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous n'avez pas la permission de voir cette adresse");
        }
        
        return mapToResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(Long id, Long userId, com.shopflow.entity.UserRole role, AddressCreateRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (role != com.shopflow.entity.UserRole.ADMIN && !address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous n'avez pas la permission de modifier cette adresse");
        }

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());

        if (request.getIsPrimary() != null && request.getIsPrimary()) {
            address.getUser().getAddresses().forEach(a -> a.setIsPrimary(false));
            address.setIsPrimary(true);
        }

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    @Transactional
    public void deleteAddress(Long id, Long userId, com.shopflow.entity.UserRole role) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (role != com.shopflow.entity.UserRole.ADMIN && !address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous n'avez pas la permission de supprimer cette adresse");
        }

        addressRepository.delete(address);
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isPrimary(address.getIsPrimary())
                .createdAt(address.getCreatedAt())
                .build();
    }
}