INSERT INTO traccar.tc_users
(id, name, email, hashedpassword, salt, readonly, administrator, `map`, latitude, longitude, zoom, `attributes`, coordinateformat, disabled, expirationtime, devicelimit, userlimit, devicereadonly, phone, limitcommands, login, poilayer, disablereports, fixedemail, totpkey, `temporary`)
VALUES(1, 'Jaisen Ivan Larralde Ortiz', 'larralde.ortiz.jaisen@gmail.com', 'efe6e1ec50374ad04601f0bb883b88bf6a34cd752e313e53', '4ec5b5859ccf67f274a997449ac9da53fe2046543646a618', 0, 1, NULL, 0.0, 0.0, 0, '{}', NULL, 0, NULL, -1, 0, 0, NULL, 0, NULL, NULL, 0, 0, NULL, 0);


INSERT INTO traccar.tc_clients
(id, name, email, rfc, `attributes`)
VALUES(1, 'Ochoa', 'LAOJ950909MJ4', '12345678', '{}');


INSERT INTO traccar.tc_user_client
(clientid, userid)
VALUES(1, 1);
