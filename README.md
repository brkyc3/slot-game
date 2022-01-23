# slot-game

yüzdelerin birebir eşlenmesi shuffle ile sağlandı,100'ün altında birebir sağlamıyor ancak 100'e ulaştığında sayılar birebir tutuyor yüzdelerle

dbde mongodb kullandım, ama embedded mongodbde bi bug var şuan, o yüzden mongodb://localhost:27017 ile kullanabilirsiniz, ya da yaml'dan ayarlayabilirsiniz uri bilgisini

alttaki idler projeyi çalıştırdığımızda ekleniyor test playerlari için 

61ec0a9e1165241fe2e61fc9
61ec646c1165241fe2e61fd4
61ec646c1165241fe2e61fd4

socket bağlantıları alttaki üç path üzerinden gerçekleşiyor, spin ve auth karşılıklı mesajlaşma theft ise sunucu tarafından gönderilen bildirimler için kullanılıyor

sockjs ya da socketio kullanmadım, raw websocket ile yazdım

request ve response isterlerdeki alanlar ile json olarak gönderiliyor

ws://localhost:8991/spin 

ws://localhost:8991/auth

ws://localhost:8991/theft

request

{"playerId":"61ec646c1165241fe2e61fd4"}

authResponse

{"playerId":"61ec646c1165241fe2e61fd4","spinAmount":7,"coinAmount":5000}

thiefNotificationResponse

{"currentCoinAmount":500}
