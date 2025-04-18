(ns clojure-owasp.owasp10)

(def db {:patrick.noronha "banana", :marcelo.reis "senha"})

(defn login [ip username password]
  (let [found-password (get db (keyword username))]
    (= found-password password)))

(println (login "123.54.2.12" "patrick.noronha" "banana"))
(println (login "123.54.2.12" "patrick.noronha" "senha"))

(println (dotimes [_ 1000] (login "123.54.2.12" "patrick.noronha" "89jr43")))

(def username-attempts (atom {}))
(def ip-attempts (atom {}))

(defn my-inc [x]
  (if x (inc x)
        1))

(def login-limit 30)

(defn attempt-login? [ip username]
  (swap! username-attempts update-in [username] my-inc)
  (swap! ip-attempts update-in [ip] my-inc)
  (and (<= (get @ip-attempts ip) login-limit)
       (<= (get @username-attempts username) login-limit)))

;(println (attempt-login? "patrick.noronha" ))
;(println (attempt-login? "patrick.noronha" ))

;(println (dotimes [_ 50] (println (login "patrick.noronha" "89jr43"))))


(defn login [ip username password]
  (let [keyword-username (keyword username)]
    (if (attempt-login? ip keyword-username)
      (let [found-password (get db keyword-username)]
        (if (= found-password password)
          (do (swap! username-attempts update-in [keyword-username] * 0)
              (swap! ip-attempts update-in [ip] * 0)
              true)
          false))
      (throw (Exception. "Ha! Too many attempts!")))))


(println (dotimes [_ 29] (login "123.54.2.12" "patrick.noronha" "89jr43")))
(println @username-attempts)
(println (login "123.54.2.12" "patrick.noronha" "banana"))
(println @username-attempts)


(println (dotimes [_ 29] (login "123.54.2.12" "patrick.noronha" "89jr43")))
(println (dotimes [_ 1] (login "123.54.2.12" "marcelo.reis" "89jr43")))
(println @username-attempts)
(println @ip-attempts)
(println (dotimes [_ 1] (login "123.54.2.12" "marcelo.reis" "senha")))
