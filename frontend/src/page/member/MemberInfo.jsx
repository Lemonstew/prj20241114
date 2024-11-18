import { Box, Input, Spinner, Stack, Textarea } from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { Field } from "../../components/ui/field.jsx";
import { Button } from "../../components/ui/button.jsx";
import {
  DialogActionTrigger,
  DialogBody,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogRoot,
  DialogTitle,
  DialogTrigger,
} from "../../components/ui/dialog.jsx";

export function MemberInfo() {
  const [member, setMember] = useState(null);
  const { id } = useParams();

  useEffect(() => {
    // 회원 정보 열기
    axios.get(`/api/member/${id}`).then((res) => setMember(res.data));
  }, []);

  function handleDeleteClick() {
    axios.delete("/api/member/remove", {
      data: { id: id },
    });
  }

  if (!member) {
    return <Spinner />;
  }

  //?? console.log(id);
  return (
    <Box>
      <h3>회원 정보</h3>
      <Stack gap={5}>
        <Field label={"아이디"}>
          <Input readOnly value={member.id} />
        </Field>
        <Field label={"암호"}>
          <Input readOnly value={member.password} />
        </Field>
        <Field label={"자기소개"}>
          <Textarea readOnly value={member.description} />
        </Field>
        <Field label={"가입일시"}>
          <Input type={"datetime-local"} readOnly value={member.inserted} />
        </Field>
        <Box>
          <DialogRoot>
            <DialogTrigger>
              <Button colorPalette={"red"} variant={"outline"}>
                탈퇴
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>탈퇴 확인</DialogTitle>
              </DialogHeader>
              <DialogBody>
                <p>탈퇴하시겠습니까?</p>
              </DialogBody>
              <DialogFooter>
                <DialogActionTrigger>
                  <Button variant={"outline"}>취소</Button>
                </DialogActionTrigger>
                <Button colorPalette={"red"} onClick={handleDeleteClick}>
                  탈퇴
                </Button>
              </DialogFooter>
            </DialogContent>
          </DialogRoot>
        </Box>
      </Stack>
    </Box>
  );
}
