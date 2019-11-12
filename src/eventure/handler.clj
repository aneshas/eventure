(ns eventure.handler
  (:require [eventure.app :as app]
            [eventure.infra :as infra]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

; TODO - Construct URI (also return map with two keys - uri and event?
; TODO - Return response (URI in location header)
(defn create-event [request]
  (app/create-event infra/save-to-datastore (:body request)))

(defroutes app-routes
           (POST "/events" create-event)
           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

