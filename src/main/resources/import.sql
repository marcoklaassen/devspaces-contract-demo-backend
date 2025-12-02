INSERT INTO `contract`(id, `type`, customer, firstContractOfCustomer) VALUES (1, 'health', 'Marco', true);
INSERT INTO `contract`(id, `type`, customer, firstContractOfCustomer) VALUES (2, 'car', 'John', true);
ALTER SEQUENCE contract_seq RESTART WITH 3;