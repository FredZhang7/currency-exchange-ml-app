/*
> SELECT Date, Value FROM CurrencyData WHERE Currency = 'Canadian dollar';
is safe as long as the currency name is not a variable, because an user can
inject a malicious script (e.g. "'Canadian dollar'; DROP TABLE CurrencyData;")
*/



DELIMITER //
CREATE PROCEDURE GetCurrencyHistory(IN currency VARCHAR(255))
BEGIN
    SELECT Date, Value FROM CurrencyData WHERE Currency = currency;
END //
DELIMITER ;

CALL GetCurrencyHistory('Canadian dollar');