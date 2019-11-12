(ns eventure.handler-test
  (:require [clojure.test :refer :all]
            [eventure.domain :as d]
            [clojure.spec.gen.alpha :as gen])
  (:import (java.util UUID)))

;(deftest test-app
;  (testing "main route"
;    (let [response (app (mock/request :get "/"))]
;      (is (= (:status response) 200))
;      (is (= (:body response) "Hello World"))))
;
;  (testing "not-found route"
;    (let [response (app (mock/request :get "/invalid"))]
;      (is (= (:status response) 404)))))

; TODO - This might be more convenient with generators and specs
(deftest event-creation
  (testing "test success"
    (let [id (UUID/randomUUID)
          details {:name        "My Birthday"
                   :host        "Anes"
                   :time        "22/03/2019"
                   :description "Simple bday"}
          items [{:name        "Patka"
                  :description "Jedna patka"}
                 {:name        "Guska"
                  :description "I Guska"}]
          event (d/create-event id details items)]
      (is (and
            (= id (::d/event-id event))
            (= "My Birthday" (::d/event-name event))
            (= "Anes" (::d/event-host event))
            (= "22/03/2019" (::d/event-time event))
            (= "Simple bday" (::d/event-description event))
            )))))

;{::event-id          id
; ::event-created-at  (Date.)
; ::event-admin-hash  (generate-admin-hash id)
; ::event-items       (map (fn [i] {::item-id          (UUID/randomUUID)
;                                   ::item-name        (:name i)
;                                   ::item-description (:description i)}) items)}