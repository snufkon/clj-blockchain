(ns clj-blockchain.test-data)

(def transaction1
  {:sender "8527147fe1f5426f9dd545de4b27ee00"
   :recipient "a77f5cdfa2934df3954a5c7c7da5df1f"
   :amount 5})

(def transaction2
  {:sender "8527147fe1f5426f9dd545de4b27ee00"
   :recipient "a77f5cdfa2934df3954a5c7c7da5df1f"
   :amount 1})

(def block1
  {:index 1,
   :timestamp 1525039420813,
   :transactions [],
   :proof 100,
   :previous-hash "1"})

(def block2
  {:index 2,
   :timestamp 1525039421579,
   :transactions
   [{:sender "s109z0X5xVbs0Y",
     :recipient "L2W6U1Ke06gfZ5Q3r2O6Te2",
     :amount 352}
    {:sender "", :recipient "ckN0", :amount 1}
    {:sender "nZ3e7IYSD86B1f14m6La6JCHX2mdV8",
     :recipient "8v30Ih42W",
     :amount 6}
    {:sender "TEw30c2Pwe7R6X88",
     :recipient "f383Zmqk1M9K4kj307X68",
     :amount 127945434}
    {:sender "7sMzp7q6Y02tLyKs6b259qI90",
     :recipient "19MFedD4E97E0FOgd19xQ7RD",
     :amount 13551}
    {:sender "YG9u22mAgs5Hdc7t0960pd1R76uKU",
     :recipient "Pcb0T47vB96VX4t2lJZn4qVK6i",
     :amount 22710601}
    {:sender "10v2Igi4jw0C8bVp4fbUNLpX55", :recipient "", :amount 140}
    {:sender "Go0DehdJTB27f5z",
     :recipient "Z81gAhz0wZFa1t200O3a24lo7Wk",
     :amount 35}
    {:sender "v325PPcn2j3ynUCM8dQD7I610vqiD8",
     :recipient "Sa1Kmr0R57j9ZbhW2v42Hnl6h0N7s",
     :amount 14450}
    {:sender "G237t7j2wSuO5bP2c7N1v6B6a0x3NU",
     :recipient "P",
     :amount 1048233}
    {:sender "oPolP38VDT7EQNFNX", :recipient "74Sawz9N", :amount 309}
    {:sender "PbPpBs",
     :recipient "z8jkCnZe25xQ3p86MY9052e",
     :amount 2}
    {:sender "z315U9TG0", :recipient "3GIV0UOiYQ0L42E8sIS", :amount 9}
    {:sender "F87pd8yG03",
     :recipient "Ak3TxL6VM72sr1JCx397iEdo4uO",
     :amount 103974}
    {:sender "J39BLaf2CrIi9W1Jm8fU0749R8",
     :recipient "e8bP4Z6xxDKIk25Z1",
     :amount 4523322}
    {:sender "qnV8UppsmfhzBVO5xV9CVWtQ5",
     :recipient "996GjMrooksDBMgs99B6f5ESZzxj0",
     :amount 2441}
    {:sender "ux81BOpU65J3o86Y", :recipient "5Q", :amount 54257916}
    {:sender "MvlAvu5tqn",
     :recipient "J6Y17H4beU22wCS",
     :amount 713219}],
   :proof 35293,
   :previous-hash
   "e6d901643be68e2dfd96064377f3e0bbd21544f41c7765a12c789cfee624a462"})

(def block3
  {:index 3,
   :timestamp 1525039421764,
   :transactions
   [{:sender "osyEi6rUv3tGk7Eaf",
     :recipient "9xdl8cOV34zo",
     :amount 196668908}],
   :proof 35089,
   :previous-hash
   "ef42a79faf0a2b670e6675ffc53fc056b322e49be27563a7de20d0051b657abf"})

(def chain [block1 block2 block3])
