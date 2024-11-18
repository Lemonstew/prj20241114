import { Box, Input, Spinner, Stack, Textarea } from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { Field } from "../../components/ui/field.jsx";
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
import { Button } from "../../components/ui/button.jsx";

export function MemberEdit() {
  const { id } = useParams();
  const [member, setMember] = useState(null);
  const [password, setPassword] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [description, setDescription] = useState("");
  const [open, setOpen] = useState(false);

  useEffect(() => {
    axios.get(`/api/member/${id}`).then((res) => {
      setMember(res.data);
      setPassword(res.data.password);
      setDescription(res.data.description);
    });
  }, []);

  function handleSaveClick() {
    axios
      .put("/api/member/edit", {
        id: member.id,
        password: password,
        description: description,
        oldPassword: oldPassword,
      })
      .then()
      .catch()
      .finally();
  }

  if (member === null) {
    return <Spinner />;
  }

  return (
    <Box>
      <h3>회원 정보</h3>
      <Stack gap={5}>
        <Field readOnly label={"아이디"}>
          <Input defaultValue={member.id} />
        </Field>
        <Field label={"암호"}>
          <Input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </Field>
        <Field label={"자기소개"}>
          <Textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </Field>
        <Box>
          <DialogRoot open={open} onOpenChange={(e) => setOpen(e.open)}>
            <DialogTrigger>
              <Button colorPalette={"blue"} variant={"outline"}>
                저장
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>회원 정보 변경 확인</DialogTitle>
              </DialogHeader>
              <DialogBody>
                <Stack gap={5}>
                  <Field label={"기존 암호"}>
                    <Input
                      placeholder={"기존 암호를 입력해주세요."}
                      value={oldPassword}
                      onChange={(e) => setOldPassword(e.target.value)}
                    />
                  </Field>
                </Stack>
              </DialogBody>
              <DialogFooter>
                <DialogActionTrigger>
                  <Button variant={"outline"}>취소</Button>
                </DialogActionTrigger>
                <Button colorPalette={"blue"} onClick={handleSaveClick}>
                  저장
                </Button>
              </DialogFooter>
            </DialogContent>
          </DialogRoot>
        </Box>
      </Stack>
    </Box>
  );
}
