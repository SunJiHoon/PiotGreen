package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.entity.ManagerData;
import com.piotgreen.piotgreen.repository.ManagerDataRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerDataStorageService {
    private final SendMessageService sendMessageService;
    private final ManagerDataRepository managerDataRepository;

    public List<ManagerData> getAllManagerData() {
        List<ManagerData> managerDataList = managerDataRepository.findAll();
        return managerDataList;
    }
    public void sendMessageToAllManger(String message) {
        List<ManagerData> managerDataList = managerDataRepository.findAll();
        for (ManagerData managerData : managerDataList) {
            sendMessageService.sendMessage(managerData.getPhoneNumber(), message);
        }
    }
    public void saveUser(ManagerData managerData) {
        managerDataRepository.save(managerData);
    }
    @Transactional // 트랜잭션 활성화
    public void deleteByPhoneNumber(String phoneNumber) {
        managerDataRepository.deleteByPhoneNumber(phoneNumber);
    }
}
