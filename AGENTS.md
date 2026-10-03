# Kullanıcının kelimesi kelimesine sohbet kaydı talebi

- Her yeni kullanıcı mesajında `/Users/fatih/Projects/avelrune/scripts/update-conversation-cache.py` betiğini çalıştır. Betik erişilebilir yerel Codex sohbetlerinin kullanıcı/asistan mesaj olaylarını kaynak oturum günlüklerinden tam `payload` içeriği ve gerçek zaman damgalarıyla `/Users/fatih/Projects/avelrune/.conversation-cache/all-codex-messages.jsonl` dosyasına kopyalar.
- Kullanıcının veya asistanın mesajlarını özetleyip kalıcı bellek yerine koyma. Önceki mesaj gerektiğinde arşivdeki kaynak `payload.content` metnini aynen oku.
- Kullanıcı her promptta arşivin tamamının baştan sona okunmasını ve zaman sırasıyla değerlendirilmesini istedi. Bağlam veya araç sınırı buna engelse bunu açıkça belirt; tamamını okumuş gibi yapma.
- Her yanıtın gönderiminden hemen önce kullanıcı mesajı ve önceki asistan mesajları için arşivi yenile. Son yanıt metninin birebir bir kopyasını ayrıca hazırlanma zamanıyla kaydet; sonraki turda gerçek oturum olayı bu geçici kaydın yerini alsın. Her yanıtta kayıt durumunu açıkça söyle; başarısızsa söyle.
- Kullanıcının ekran/fare kontrolü ve alt ajan kullanımına ilişkin mevcut açık yasaklarına uy.
