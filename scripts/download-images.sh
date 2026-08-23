#!/usr/bin/env bash
ROOT="/Users/fatih/Projects/avelrune/frontend/public/images"
mkdir -p "$ROOT"/{properties,regions,agents,about}

download() {
  local url="$1"
  local out="$2"
  if [[ -s "$out" ]]; then
    echo "OK exists $out"
    return 0
  fi
  if curl -fsSL --retry 2 --retry-delay 1 "$url" -o "$out"; then
    echo "OK $out"
    return 0
  fi
  rm -f "$out"
  echo "FAIL $out"
  return 1
}

photo() {
  echo "https://images.unsplash.com/${1}?auto=format&fit=crop&w=${2:-1600}&q=80"
}

download "$(photo photo-1613490493576-7fde63acd811 2000)" "$ROOT/hero.jpg"
download "$(photo photo-1600585154340-be6161a56a0c 1600)" "$ROOT/about/atelier.jpg"
download "$(photo photo-1600607687939-ce8a6c25118c 1400)" "$ROOT/about/detail.jpg"

download "$(photo photo-1541432901042-2f0b28cd87d4 1400)" "$ROOT/regions/istanbul.jpg"
download "$(photo photo-1615571020779-313309f574fb 1400)" "$ROOT/regions/bodrum.jpg"
download "$(photo photo-1507525428034-b723cf961d3e 1400)" "$ROOT/regions/cesme.jpg"
download "$(photo photo-1500382017468-9049fed747ef 1400)" "$ROOT/regions/urla.jpg"
download "$(photo photo-1544550581-5f7ceaf7f992 1400)" "$ROOT/regions/antalya.jpg"

download "$(photo photo-1573496359142-b8d87734a5a2 900)" "$ROOT/agents/selin.jpg"
download "$(photo photo-1472099645785-5658abf4ff4e 900)" "$ROOT/agents/deniz.jpg"
download "$(photo photo-1500648767791-00dcc994a43e 900)" "$ROOT/agents/mirac.jpg"
download "$(photo photo-1580489944761-15a19d654956 900)" "$ROOT/agents/aylin.jpg"

ids=(
  photo-1600596542815-ffad4c1539a9
  photo-1600585154340-be6161a56a0c
  photo-1600607687939-ce8a6c25118c
  photo-1600566753086-00f18fb6b3ea
  photo-1600210492486-724fe5c67fb0
  photo-1600047509807-ba8f99d2cdbc
  photo-1512917774080-9991f1c4c750
  photo-1613490493576-7fde63acd811
  photo-1600573472592-401b489a3cdc
  photo-1600585154526-990dced4db0d
  photo-1564013799919-ab600027ffc6
  photo-1600047509358-9dc75590a4e0
  photo-1613977257363-707ba9348227
  photo-1600566753190-17f0baa2a6c3
  photo-1600607687644-c7171b42498b
  photo-1502672260266-1c1ef2d93688
  photo-1560448204-e02f11c3d0e2
  photo-1600585154363-67eb9e2e2099
  photo-1618221195710-dd6b41faaea6
  photo-1600121848594-d8644e57abab
  photo-1600566752355-331795482e88
  photo-1554995207-c18c203602cb
  photo-1560185893-a55cbc8c57bb
  photo-1600585153490-76fb20a32601
  photo-1572120360610-6afda532bedb
  photo-1605276374104-7369fe93e5b0
  photo-1616137466211-f939a420be84
  photo-1600210491892-03d54c0aaf87
  photo-1416331108676-a22ccb276e35
  photo-1484154216822-a71222c75d5c
  photo-1493809842364-78817add7ffb
  photo-1505693416388-ac5ce068fe85
  photo-1560185127-6ed189bf02f4
  photo-1600585152220-90363fe7e115
  photo-1613977257592-4871e5fcd7c4
  photo-1513584684374-8bab748fbf90
  photo-1599809275671-b5942cabc7a2
  photo-1583608205776-bfd35f0d9f83
  photo-1570129477492-45c003edd2be
  photo-1568605114967-8130f3a36994
  photo-1576941089067-2de3c901e126
  photo-1518780664697-55e3ad937233
  photo-1512918728675-ed5a9ecdebfd
  photo-1449844908441-88277401c2a8
  photo-1472224371017-08207f84aaae
  photo-1464146072230-91cabc968266
  photo-1502005097973-6a7082348e28
  photo-1600566753376-12c8ab7fb75b
  photo-1600607688969-a5abba5563e4
  photo-1593696140826-c58b021acf8b
  photo-1600566752229-250ed79402f6
  photo-1616594039964-bdfa28c444a6
  photo-1556912173-46c336c7fd55
  photo-1556909114-f6e7ad7d3136
  photo-1560448204-61dc36dc98c8
  photo-1545324418-cc1a3fa10c00
  photo-1486406146926-c627a92ad1ab
  photo-1610641818989-c4162b0f0d0c
  photo-1600607687920-4e2a09cf159d
  photo-1600585154084-4e5fe7c39198
  photo-1501183638710-841dd1904471
  photo-1493663284031-b7e3aefcae8e
  photo-1536376071030-947bce687f02
  photo-1522708323590-d24e4d0c7d0d
  photo-1582268611958-ebfd161ef9cf
  photo-1515263487990-61b07816b324
  photo-1523217582562-09d0def993a6
  photo-1628624747186-a941c476b7ef
  photo-1499793983690-e29da59ef1c2
  photo-1520250497591-112f2f40a3f4
  photo-1542314831-068cd1dbfeeb
  photo-1571896349842-33c89424de2d
  photo-1560185007-cde436f6a4d0
  photo-1616486338812-3dadae4b4ace
  photo-1618220179428-22790b461013
  photo-1615873968403-89e068629265
  photo-1540518612045-3d2ba1762065
  photo-1600210492486-724fe5c67fb0
  photo-1600047509784-31a24724eec7
  photo-1600607687644-aac4c3bbc5b0
  photo-1618221118493-9cfa1a1c7651
  photo-1600585154340-be6161a56a0c
  photo-1600566753190-17f0baa2a6c3
  photo-1600573472592-401b489a3cdc
  photo-1560448075-bb485b067938
  photo-1631679706909-1844bbd07221
  photo-1615874958471-d2cdc1fa32d7
  photo-1616047006789-b7af5af4e6b3
  photo-1615529182904-14819c35db37
  photo-1600210492486-724fe5c67fb0
)

n=0
failed=0
for p in $(seq 1 18); do
  key=$(printf "p%02d" "$p")
  for s in 1 2 3 4 5; do
    id="${ids[$n]}"
    n=$((n + 1))
    if ! download "$(photo "$id" 1600)" "$ROOT/properties/${key}-${s}.jpg"; then
      failed=$((failed + 1))
    fi
  done
done

echo "files=$(find "$ROOT" -type f | wc -l) failed=$failed"
