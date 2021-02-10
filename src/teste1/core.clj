(ns teste1.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))


; Simple Body Page
(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str "Name: Nome Aleatorio "
                 "Values: R$ 999.99 "
                 "Date: 09.02.2021")})

; request-transactions
(defn request-transactions [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (->
              (pp/pprint req)
              (str "Nome do Cliente 1: " (:name (:params req))
                   "<br/> Valores: " (:value (:params req))
                   "<br/> Data: " (:date (:params req))))})

; transactions-collection
(def transactions-collection (atom []))

; add new transaction
(defn addtransaction [name value date]
  (swap! transactions-collection conj {:name (str/capitalize name)
                                       :value (str/capitalize value)
                                       :date (str/capitalize date)}))
; Example transactions JSON
(addtransaction "Milena" "100" "02.03.2020")
(addtransaction "Lucas" "9.53" "07.06.2012")
(addtransaction "Liz" "72.04" "10.02.2021")

; Return List of transactions
(defn trasactions-handler [req]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (str (json/write-str @transactions-collection))})

; Our main routes
(defroutes app-routes
           (GET "/" [] simple-body-page)
           (GET "/transactions" [] request-transactions)
           (GET "/transactionslist" [] trasactions-handler)
           (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point."
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
