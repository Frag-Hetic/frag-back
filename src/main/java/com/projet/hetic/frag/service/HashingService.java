package com.projet.hetic.frag.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.exception.EntityNotFoundException;

@Service
public class HashingService {

  public String hashAndCrypt64(byte[] bitesArray) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(bitesArray);
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        hexString.append(String.format("%02x", b));
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new EntityNotFoundException("Hash", "algorithm", "SHA-256");
    }
  }

  public boolean compareConstructFileWithCheckHash(byte[] constructFile, String checkHash) {
    String fileContentHash = hashAndCrypt64(constructFile);
    return fileContentHash.equals(checkHash);
  }
}