/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bharatividyapeeth.transcriptmailingsystem.Exception;

/**
 *
 * @author ShAd0w
 */
public class TranscriptException extends Exception {

    public TranscriptException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
    
    public TranscriptException(String errorMessage) {
        super(errorMessage);
    }
}
