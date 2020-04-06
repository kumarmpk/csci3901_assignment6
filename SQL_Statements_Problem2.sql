drop table purchases;

CREATE TABLE purchases (
  purchaseid int(11) NOT NULL,
  productid int(11) NOT NULL,
  supplierid int(11) NOT NULL,
  purchasedate date,
  unitprice decimal(10,4) DEFAULT 0.0000,
  quantity smallInt(2) DEFAULT 1,
  shipvia int(11),
  shipdate date,
  shipid int(11),
  receivedate date,
  PRIMARY KEY (purchaseid, productid),
  FOREIGN KEY (productid) REFERENCES products(productid),
  FOREIGN KEY (supplierid) REFERENCES products(supplierid)
);