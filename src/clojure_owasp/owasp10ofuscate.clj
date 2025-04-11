(ns clojure-owasp.owasp10
  (:require [clojure.string :as str])
  (:import [java.security MessageDigest]))

(defn hash-str [s]
  (let [digest (MessageDigest/getInstance "SHA-256")]
    (->> (.digest digest (.getBytes s "UTF-8"))
         (map #(format "%02x" %))
         (apply str))))

;; Ofuscar nomes de usuário e senhas
(def db {(keyword (hash-str "patrick.noronha")) (hash-str "banana")
         (keyword (hash-str "marcelo.reis")) (hash-str "senha")})

(def username-attempts (atom {}))
(def ip-attempts (atom {}))
(def login-limit 30)

(defn my-inc [x]
  (if x (inc x) 1))

(defn attempt-login? [ip username]
  (swap! username-attempts update-in [username] my-inc)
  (swap! ip-attempts update-in [ip] my-inc)
  (and (<= (get @ip-attempts ip) login-limit)
       (<= (get @username-attempts username) login-limit)))

(defn login [ip username password]
  (let [hashed-username (keyword (hash-str username))
        hashed-password (hash-str password)]
    (if (attempt-login? ip hashed-username)
      (let [found-password (get db hashed-username)]
        (if (= found-password hashed-password)
          (do (swap! username-attempts update-in [hashed-username] * 0)
              (swap! ip-attempts update-in [ip] * 0)
              true)
          false))
      (throw (Exception. "Ha! Too many attempts!")))))

;; Testes
(println (login "123.54.2.12" "patrick.noronha" "banana"))
(println (login "123.54.2.12" "patrick.noronha" "senha"))

(dotimes [_ 29] (login "123.54.2.12" "patrick.noronha" "89jr43"))
(println @username-attempts)
(println (login "123.54.2.12" "patrick.noronha" "banana"))
(println @username-attempts)

(dotimes [_ 29] (login "123.54.2.12" "patrick.noronha" "89jr43"))
(login "123.54.2.12" "marcelo.reis" "89jr43")
(println @username-attempts)
(println @ip-attempts)
(login "123.54.2.12" "marcelo.reis" "senha")
