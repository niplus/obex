package com.fota.android.moudles.market.bean;


import java.io.Serializable;

public class CardItemParamBean implements Serializable {
    String cardId;
    String cardType;
    String cardName;

    public CardItemParamBean(String cardId, String cardType, String cardName) {
        this.cardId = cardId;
        this.cardName = cardName;
        this.cardType = cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
