require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: dateTime/dateTime.sc
    module = sys.zb-common
require: functions.js
require: patterns.sc

theme: /

    state: RussiaAbroad
        q!: $regex</start>
        # script:
            # getWelcomeMessage();
        a: Добрый день. Это компания <sil[200]>.
    #    audio: https://storage.yandexcloud.net/bot-for-website/TruckingBot/%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C1.wav
        a: Подскажите, пожалуйста, у Вас грузоперевозка в пределах Российской Федерации?
    #    audio: https://storage.yandexcloud.net/bot-for-website/TruckingBot/%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C2.wav

        script:
            // var get_calls_url = 'https://d5dkebnehldv7u12d8v8.apigw.yandexcloud.net/getCalls';
            // var response = $http.get(get_calls_url);
            // if (response.isOk) {
                // $session.lastCalls = response.data;
                // $session.lastCalls = $session.lastCalls.split(',');
                // var lastCalls = [];
                // for (var i = 0; i < $session.lastCalls.length; i ++) {
                    // if (!($session.lastCalls[i] in lastCalls)) {
                        // lastCalls.push(phoneEdit($session.lastCalls[i]));
                        // }
                    // };
                // log('@@@' + lastCalls);
                // log('!!!' + lastCalls[0]);
            // };

        state: ClientAnswerRussia
            intent: /Russia
            intent: /yes
            go!: ../../CargoVolume

        state: ClientAnswerAbroad
            intent: /abroad
            intent: /no
            a: Извините, мы перевозим только по России. До свидания.
            # Пока нет аудио.
            go!: ../../BotHangup


    state: CargoVolume
        a: Объем груза больше двух кубических метров?
    #    audio: https://storage.yandexcloud.net/bot-for-website/TruckingBot/%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C3.wav
    #    script:
    #        $session.cargoVolume = $parseTree._

        state: ClientAnswerCargoMore
            intent: /yes
            intent: /more
            go!: ../../TruckingDistance

        state: ClientAnswerCargoLess
            intent: /no
            intent: /less
            a: Извините, мы не работаем с грузами меньшего объема. До свидания.
        #    audio: https://storage.yandexcloud.net/bot-for-website/TruckingBot/%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C6.wav
            go!: ../../BotHangup


    state: TruckingDistance
        a: А расстояние перевозки более ста километров?
    #    audio: https://storage.yandexcloud.net/bot-for-website/TruckingBot/%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C4.wav
        script:
            # Создание пушбэка
            var pushback = $pushgate.createPushback(
                $request.channelType,
                $request.botId,
                $request.channelUserId,
                'newNotification',
                {'text': 'Перевозка в другой город'}
                );

        state: ClientAnswerDistanceMore
            intent: /yes
            intent: /more
            go!: ../../OperatorTransfer

        state: ClientAnswerDistanceLess
            intent: /no
            intent: /less
            # Пока нет аудио.
            a: Извините, мы не перевозим на расстояние менее ста километров. До свидания.
            go!: ../../BotHangup


    state: OperatorTransfer
        a: Отлично! Перевожу Вас на менеджера, который завершит оформление заявки.
    #    audio: https://storage.yandexcloud.net/bot-for-website/TruckingBot/%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C5.wav
    #    script:
    #        var date_time = moment($jsapi.currentTime()).format();
    #        var date_time_str = String(date_time);
            var phone = $dialer.getCaller();

    #        var post_calls_url = 'https://d5d359fa7qo1kt2t0vcl.apigw.yandexcloud.net/data_filling?date_time='
    #        + date_time + '&date_time_str=' + date_time_str + '&phone=' + phone;
    #        var params = {
    #            dataType: "json",
    #            headers: {
    #                "Content-Type": "application/json"
    #            },
    #            body: {
    #                "date_time": date_time,
    #                "date_time_str": date_time_str,
    #                "phone": phone
    #                }
            #    "queryStringParameters":
            #            {
            #                "date_time": date_time,
            #                "date_time_str": date_time_str,
            #                "phone": phone
            #            }
    #            };
    #        var response_ = $http.post(post_calls_url, params);
    #        log('RESPONSE STATUS: ' + response.status);
    #        if (response_.isOk) {
    #            log('***ERROR WHILE POSTING***');
    #            }
    #        else {
    #            log('***POSTING SUCCESSFULL***');
    #            };
        go!: ../BotHangup


    state: BotHangup
        script:
            $response.replies = $response.replies || [];
            $response.replies.push({
                type: "karta",
                text: $dialer.getCallRecordingFullUrl()
            });
            
            $dialer.hangUp();


    state: ClientHangup
        event!: hangup
        script: $response.replies = $response.replies || [];
                $response.replies.push({
                    type: "karta",
                    text: $dialer.getCallRecordingFullUrl()
                });
            
                $dialer.hangUp();


    state: NoMatch
        event!: noMatch
        # Пока нет аудио.
        a: Прошу прощения, не совсем поняла Вас. Сформулируйте, пожалуйста, по-другому.


    state: Null
        event!: speechNotRecognized
        # Пока нет аудио.
        a: Извините, не расслышала. Повторите, пожалуйста.

    state: CurrentTime
        q!: который час
        script:
            var timestamp = moment($jsapi.currentTime());
            $temp.time = timestamp.format();
        a: Сейчас: {{ $temp.time }}

    state: CurrentTime2
        q!: * {сколько * врем*} *
        script:
            $temp.time2 = currentDate().locale('ru').format('DD.MM.YYYY');
        a: Сейчас: {{ $temp.time2 }}.

    state: SendFile
        q!: * {отправ* * файл*} *
        script:
            var link = "https://disk.yandex.ru/d/4IzFffNDQT25-A";
            $http.post(
                link, {
                    timeout: 10000,
                    fileUrl: 'https://drive.google.com/file/d/1KEvZKkHHq0_8ozoOIiIzMB7AbbVfg9ww/view?usp=share_link',
                    fileName: 'xt5f92prnh4.jpg'
                    });

    state: SendFile2
        q!: * file *
        script:
            $response.replies = $response.replies || [];
            $response.replies.push({
                "type": "raw",
                "body": {
                    "chat_id": $request.rawRequest.message.chat.id,
                    "photo": "https://storage.yandexcloud.net/bot-for-website/Images_test/xt5f92prnh4.jpg" //вставить идентификатор файла на сервере tg
                },
                "method": "sendPhoto"
            });

    state: hiTest
        q!: * $hi::hi *
        script:
            $session.pattern = $parseTree.pattern;
            log('@@@' + toPrettyString($parseTree));
        a: {{ $session.pattern }}

    state: byeTest
        intent!: /bye
        script:
            $session.intent = $context.intent.path;
            log('###' + toPrettyString($context));
        a: {{ $session.intent }}


    state: ChildrenTest1
        q!: * test* *
        go!: ../ChildrenTest2


    state: ChildrenTest2
        # script:
            # if ($context.intent.path === "/operator") {
                # $reactions.transition(( {value: "./Operator", deferred: false} ));
                # } else {
                    # $reactions.transition(( {value: "./Comment", deferred: false} ));
                    # };

        state: Comment
            event: noMatch
            event: speechNotRecognized
            script:
                $session.testComment = $parseTree.text;
                $reactions.answer("OK, I got it!");
                $reactions.answer('{{$session.testComment}}');

        state: Operator
            intent: /operator
            script:
                $session.testComment = 'operator';
                $reactions.answer("OK, I will transfer the call to the operator!");
                $reactions.answer('{{$session.testComment}}');