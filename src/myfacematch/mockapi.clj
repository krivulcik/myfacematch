(ns myfacematch.mockapi
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
	    [clojure.data.json :as json]
	    [clj-jwt.core :as jwt]))


(defn images [req]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str
     {:token (-> {:iss "myfacematch" :mfdata {:sessionid "abcdefghij" :id1 1 :id2 2}} jwt/jwt (jwt/sign :HS256 "mockapisecret") jwt/to-str)
      :images [{:url "/images/1.jpg" :name "Ygritte" :info "S01E01@12:34"} {:url "/images/2.jpg" :name "Catelyn Stark" :info "S01E01@01:23"}]})})

(defn vote [req]
      (try
        (if
          (-> (get-in req [:body :token]) jwt/str->jwt (jwt/verify "mockapisecret"))
          {:status 200
            :headers {"Content-Type" "application/json"}
            :body (json/write-str "success")}
	  {:status 400
            :headers {"Content-Type" "application/json"}
	    :body (json/write-str "fail: unable to verify JWT token: verification failed")})
        (catch Exception e
	  {:status 400
            :headers {"Content-Type" "application/json"}
	    :body (json/write-str (str "fail: unable to verify JWT token: " (.getMessage e)))})))
