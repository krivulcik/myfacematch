(ns myfacematch.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
	    [ring.middleware.json :as json-middle]
	    [clojure.data.json :as json]
	    [clj-jwt.core :as jwt]
	    [myfacematch.mockapi :as mockapi]))

(defn myfunc [path] {:status 200 :headers {"Content-Type" "text/plain"} :body (str path)})

(defroutes app-routes
  (GET "/" [] "Hello World")
  (context "/mockapi/v1" []
    (GET "/images" [] mockapi/images)
    (POST "/vote/:id" [] mockapi/vote))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (json-middle/wrap-json-body (handler/site app-routes) {:keywords? true}))
